;------------------------------------------------------------------------------
; 16 bit disassembler for DOS
; by Oleg Smelov, 2011
;
; d.asm
;------------------------------------------------------------------------------

.model small
.stack 200h
locals __

include macros.inc

.data
        include data.inc
        
        file_name_len   equ 100h
        file_name       db file_name_len dup (?)
        file_handle     dw ?
        
        buffer_len      equ 100
        buffer          db buffer_len dup (?)
        buffer_size     dw ?
        
        override_seg    db ?            ; 0/1 - segment overriden
        current_seg     db ?
        
        segment_pos     dw ?
        segment_max     dw ?            ; max position of a segment (the end)
        
        current_type    dw ?            ; command type
        current_mnem    dw ?            ; command mnemonics
        current_op1     dw ?            ; first operand
        current_op2     dw ?            ; second operand
        current_len     dw ?            ; opcode length
        
        max_opcode_len  equ 6           ; longest opcode this disassembler is
                                        ; aware of
.code

;------------------------------------------------------------------------------
; FUNCTIONS
;------------------------------------------------------------------------------

        parse_params proc
                push    dx si di
                mov     si, 81h                 ; argument address
                mov     di, offset file_name
        
        __copy_argument:
                mov     dl, es:[si]
                inc     si
                
                cmp     dl, 0Dh                 ; CR marks the end of arguments
                je      __end
                
                cmp     dl, ' '
                je      __copy_argument
                
                mov     byte ptr [di], dl
                inc     di
                jmp     __copy_argument

        __end:
                mov     byte ptr [di], 0        ; null byte at the end
                pop     di si dx
                ret
        endp
;------------------------------------------------------------------------------
        ; cx:dx - file position
        file_seek proc
                push    ax bx
                mov     ax, 4200h               ; seek from the start
                mov     bx, [file_handle]
                int     21h
                pop     bx ax
                ret
        endp
;------------------------------------------------------------------------------
        ; dx (positive) - number of bytes to rewind
        file_rewind proc
                push    ax bx cx dx
                mov     ax, 4201h               ; seek from the current position
                mov     bx, [file_handle]
                mov     cx, 0FFFFh
                neg     dx
                int     21h
                pop     dx cx bx ax
                ret
        endp
;------------------------------------------------------------------------------
        ; cx - length
        ; dx - buffer
        ; returns ax - number of bytes read
        ; cf set on eof or error
        file_read proc
                push    bx
                mov     ah, 3Fh
                mov     bx, [file_handle]
                int     21h
                
                or      ax, ax
                ja      __not_eof
                
                stc     ; set carry flag
                jmp     __end
                
        __not_eof:
                clc     ; clear carry flag
        __end:
                pop     bx
                ret
        endp
;------------------------------------------------------------------------------
        print_asciz proc
                push    bp
                mov     bp, sp
                push    ax dx si
                mov     si, [bp+4]
                
        __loop:
                mov     dl, [si]
                inc     si
                cmp     dl, 0
                je      __end
                
                mov     ah, 2
                int     21h
                jmp     __loop
                
        __end:
                pop     si dx ax
                pop     bp
                ret     2
        endp
;------------------------------------------------------------------------------
        print_pointer_start proc
                push    bx
                
                cmp     ax, o_rm8
                je      __byte_ptr
                
                print_string    str_word_ptr
                jmp     __address
                
        __byte_ptr:
                print_string    str_byte_ptr
                
        __address:
                mov     bl, [override_seg]
                or      bl, bl
                jz      __no_override
                
                xor     bh, bh
                mov     bl, [current_seg]
                shl     bl, 1
                
                print_string_reg        segs[bx]
                print_char              ':'
                
                mov     [override_seg], 0
                
        __no_override:
                print_string    str_brs
                
                cmp     dl, 00b
                jne     __print_addr
                cmp     dh, 110b                ; imm address (NOT BP!)
                je      __end
                
        __print_addr:
                xor     bh, bh
                mov     bl, dh
                shl     bl, 1
                
                print_string    addrs[bx]
                
        __end:
                pop     bx
                ret
        endp
;------------------------------------------------------------------------------
        ; si - current buffer position
        process_rm proc
                push    dx
                
                mov     dl, buffer[si+1]
                mov     dh, dl
                
                shr     dl, 6                   ; mod
                and     dh, 111b                ; r/m
                
        __mem_no_offset:
                cmp     dl, 00b
                jne     __mem_byte_offset
                
                call    print_pointer_start
                
                cmp     dh, 110b                ; imm address
                jne     __end_mem
                
                mov     dl, buffer[si+2]
                mov     dh, buffer[si+3]
                call    print_hex16
                
                add     [current_len], 2
                jmp     __end_mem
                
        __mem_byte_offset:
                cmp     dl, 01b
                jne     __mem_word_offset
                
                call    print_pointer_start
                
                mov     dl, buffer[si+2]
                call    print_offset8
                
                inc     [current_len]
                jmp     __end_mem
                
        __mem_word_offset:
                cmp     dl, 10b
                jne     __reg
                
                call    print_pointer_start
                
                mov     dl, buffer[si+2]
                mov     dh, buffer[si+3]
                call    print_offset16
                
                add     [current_len], 2
                jmp     __end_mem
                
        __reg:
                xor     bh, bh
                mov     bl, dh
                shl     bl, 1
                
                cmp     ax, o_rm16
                je      __reg16
        
        __reg8:
                print_string    regs8[bx]
                jmp     __end
                
        __reg16:
                print_string    regs16[bx]
                jmp     __end
                
        __end_mem:
                print_string    str_brc
        
        __end:
                pop     dx
                ret
        endp
;------------------------------------------------------------------------------
        ; si - current buffer position
        determine_command proc
                push    ax bx dx
                
                xor     bh, bh
                mov     bl, buffer[si]                          ; opcode type
                shl     bx, 3
                
                mov     ax, opcode_table[bx]
                mov     [current_type], ax
                
                cmp     ax, op_type_unknown
                je      __error
                
                mov     dx, opcode_table[bx+4]
                mov     [current_op1], dx
                mov     dx, opcode_table[bx+6]
                mov     [current_op2], dx
                
                cmp     ax, op_type_normal
                je      __opcode_normal
                cmp     ax, op_type_short
                je      __opcode_short
                
                jmp     __opcode_group
                
        __opcode_normal:
                inc     [current_len]
                
        __opcode_short:
                mov     ax, opcode_table[bx+2]
                mov     [current_mnem], ax
                
                jmp     __noerror
        
        __opcode_group:
                cmp     ax, op_type_group
                jne     opcode_seg_over
                
                ; group opcode
                inc     [current_len]
                mov     bx, opcode_table[bx+2]
                
                xor     dh, dh
                mov     dl, buffer[si+1]
                shr     dl, 2                   ; not 3 because we need to mul by 2 after that
                and     dl, 1110b
                
                ; grp_test exception
                ; only additional opcodes 0 and 1 have a second operand
                cmp     bx, offset grp_test
                jne     __opcode_group_continue
                
                cmp     dl, 10b         ; dl is shifted to the left by 1 bit
                jbe     __opcode_group_continue
                
                mov     [current_op2], o_none
                
        __opcode_group_continue:
                
                add     bx, dx
                
                mov     dx, [bx]
                mov     [current_mnem], dx
                
                jmp     __noerror
        
        opcode_seg_over:
                mov     dl, buffer[si]
                shr     dl, 3
                and     dl, 11b
                
                mov     [override_seg], 1
                mov     [current_seg], dl
        
                jmp     __noerror
        
        __error:
                stc             ; set carry flag
                jmp     __end
        
        __noerror:
                clc             ; clear carry flag
        
        __end:
                pop     dx bx ax
                ret
        endp
;------------------------------------------------------------------------------
        ; param - ax, operand
        print_operand proc
                push    bx dx
                
                cmp     ax, o_none
                lje     __end
        
        __add_reg8:
                cmp     ax, o_add_reg8
                jne     __add_reg16
                
                xor     bh, bh
                mov     bl, buffer[si]
                and     bl, 111b
                shl     bl, 1
                
                print_string_reg        regs8[bx]
                
                jmp     __end
                
        __add_reg16:
                cmp     ax, o_add_reg16
                jne     __imm8
                
                xor     bh, bh
                mov     bl, buffer[si]
                and     bl, 111b
                shl     bl, 1
                
                print_string_reg        regs16[bx]
                
                jmp     __end
                
        __imm8:
                cmp     ax, o_imm8
                jne     __imm8_se
                
                mov     bx, [current_len]
                mov     dl, buffer[bx+si]
                xor     dh, dh
                
                call    print_hex8
                
                inc     [current_len]
                jmp     __end
        
        __imm8_se:
                cmp     ax, o_imm8_se
                jne     __imm16
                
                push    ax
                mov     bx, [current_len]
                mov     al, buffer[bx+si]
                cbw                             ; imm8 sign-extended to imm16
                mov     dx, ax
                pop     ax
                
                call    print_hex16
                
                inc     [current_len]
                jmp     __end
        
        __imm16:
                cmp     ax, o_imm16
                jne     __sreg
                
                mov     bx, [current_len]
                mov     dl, buffer[bx+si]
                mov     dh, buffer[bx+si+1]
                
                call    print_hex16
                
                add     [current_len], 2
                jmp     __end
                
        __sreg:
                cmp     ax, o_sreg
                jne     __rm8
                
                xor     bh, bh
                mov     bl, buffer[si+1]
                shr     bl, 3
                and     bl, 11b
                shl     bl, 1
                
                print_string_reg        segs[bx]
                
                jmp     __end
                
        __rm8:
                cmp     ax, o_rm8
                jne     __rm16
                
                call    process_rm
                
                jmp     __end
        
        __rm16:
                cmp     ax, o_rm16
                jne     __reg8
                
                call    process_rm
                
                jmp     __end
        
        __reg8:
                cmp     ax, o_reg8
                jne     __reg16
                
                xor     bh, bh
                mov     bl, buffer[si+1]
                shr     bl, 3
                and     bl, 111b
                shl     bl, 1
                
                print_string_reg        regs8[bx]
                
                jmp     __end
        
        __reg16:
                cmp     ax, o_reg16
                jne     __add_sreg
                
                xor     bh, bh
                mov     bl, buffer[si+1]
                shr     bl, 3
                and     bl, 111b
                shl     bl, 1
                
                print_string_reg        regs16[bx]
                
                jmp     __end
        
        __add_sreg:
                cmp     ax, o_add_sreg
                jne     __rel8
                
                xor     bh, bh
                mov     bl, buffer[si]
                shr     bl, 3
                and     bl, 11b
                shl     bl, 1
                
                print_string_reg        segs[bx]
        
                jmp     __end
                
        __rel8:
                cmp     ax, o_rel8
                jne     __rel16
                
                ; output pos
                xor     bh, bh
                mov     bl, byte ptr buffer[si+1]
                test    bl, 10000000b
                jz      __positive
                
                mov     bh, 0FFh
                
        __positive:
                
                mov     dx, [segment_pos]
                add     dx, bx
                add     dx, 2
                
                call    print_hex16
                
                inc     [current_len]
                jmp     __end
        
        __rel16:
                cmp     ax, o_rel16
                jne     __moffs8
                
                ; output pos
                mov     bx, word ptr buffer[si+1]
                mov     dx, [segment_pos]
                add     dx, bx
                add     dx, 3
                
                call    print_hex16
                
                add     [current_len], 2
                jmp     __end
                
        __moffs8:
                cmp     ax, o_moffs8
                jne     __moffs16
                
                print_string    str_brs
                
                mov     bx, [current_len]
                mov     dl, buffer[bx+si]
                xor     dh, dh
                
                call    print_hex8
                
                print_string    str_brc
                
                inc     [current_len]
                jmp     __end
        
        __moffs16:
                cmp     ax, o_moffs16
                jne     __al
                
                print_string    str_brs
                
                mov     bx, [current_len]
                mov     dl, buffer[bx+si]
                mov     dh, buffer[bx+si+1]
                
                call    print_hex16
                
                print_string    str_brc
                
                add     [current_len], 2
                jmp     __end
        
        __al:
                cmp     ax, o_al
                jne     __ax
                
                print_string    reg8_al
                
                jmp     __end
        
        __ax:
                cmp     ax, o_ax
                jne     __cl
                
                print_string    reg16_ax
                
                jmp     __end
        
        __cl:
                cmp     ax, o_cl
                jne     __one
                
                print_string    reg8_cl
                
                jmp     __end
                
        __one:
                cmp     ax, o_one
                jne     __end
                
                print_string    str_one
                
                jmp     __end
                
        __end:
                pop     dx bx
                ret
        endp
;------------------------------------------------------------------------------
        ; param dl - number
        print_hex8 proc
                push    bx
                
                xor     bh, bh
                mov     bl, dl
                shr     bl, 4
                
                print_char      b_numbers[bx]
                
                mov     bl, dl
                and     bl, 1111b
                
                print_char      b_numbers[bx]
                
                pop     bx
                ret
        endp
;------------------------------------------------------------------------------
        ; param dx - number
        print_hex16 proc
                push    ax
                
                mov     ax, dx
                mov     dl, ah
                call    print_hex8
                
                mov     dl, al
                call    print_hex8
                
                pop     ax
                ret
        endp
;------------------------------------------------------------------------------
        ; param dl - number
        print_offset8 proc
                push    dx
                
                cmp     dl, 7Fh         ; max positive byte
                jbe     __positive
                
                neg     dl              ; FIXME: what about -128?..
                print_char      '-'
                
                jmp     __start
        
        __positive:
                print_char      '+'
        
        __start:
                call    print_hex8
                
                pop     dx
                ret
        endp
;------------------------------------------------------------------------------
        ; param dx - number
        print_offset16 proc
                push    dx
                
                cmp     dx, 7FFFh       ; max positive word
                jbe     __positive
                
                neg     dx
                print_char      '-'
                
                jmp     __start
        
        __positive:
                print_char      '+'
        
        __start:
                call    print_hex16
                
                pop     dx
                ret
        endp
;------------------------------------------------------------------------------
        ; starts disassembling from current position
        disassemble proc
                ; si - current position in buffer
                
                mov     [override_seg], 0
                mov     [segment_pos], 0
                
        __loop_read:
                file_read_buffer        buffer, buffer_len
                ljc     __end_read
                
                mov     [buffer_size], ax
                mov     si, 0
                
        __loop:
                cmp     [buffer_size], buffer_len
                jb      __continue_reading
                
                mov     dx, si
                add     dx, max_opcode_len
                cmp     dx, [buffer_size]
                jbe     __continue_reading
                
                ; rewind and read again
                mov     dx, buffer_len
                sub     dx, si
                call    file_rewind
                
                jmp     __loop_read
                
        __continue_reading:
                mov     [current_len], 1
                
                call    determine_command
                jc      __unknown_command
                
                ; no need to output segment override prefixes
                cmp     [current_type], op_type_seg_over
                je      __continue
                
                ; print opcode, [tab], [op1], [', '], [op2]
                
                mov     dx, [segment_pos]
                call    print_hex16
                
                print_string    str_semicolon
                
                mov     ax, [current_mnem]
                print_string_reg        ax
                
                mov     ax, [current_op1]
                cmp     ax, o_none
                je      __end_command
                
                print_string    str_tab
                
                call    print_operand
                
                mov     ax, [current_op2]
                cmp     ax, o_none
                je      __end_command
                
                print_string    str_comma
                
                call    print_operand
                
                jmp     __end_command
                
        __unknown_command:
                print_string    str_unknown
                
                mov     dl, buffer[si]
                call    print_hex8
                
                mov     [override_seg], 0
                
        __end_command:
                print_string    str_endl
                
        __continue:
                mov     ax, [current_len]
                add     si, ax
                add     [segment_pos], ax
                jc      __end_read
                
                mov     ax, [segment_pos]
                cmp     ax, [segment_max]
                jae     __end_read
                
                cmp     si, [buffer_size]
                ljb     __loop
                
        __end:
                cmp     [buffer_size], buffer_len
                lje     __loop_read

        __end_read:
                ret
        endp
;------------------------------------------------------------------------------
        ; dx:ax - code size
        get_code_size proc
                ; code_size = ([04h] - 1) * 512 + ([02h] or 512 if zero) - ([08h] + [16h]) * 16
                
                push    ax bx dx
                
                file_read_pos   04h
                ljc     __read_error
                
                dec     ax
                mov     bx, 512
                mul     bx
                
                push    dx ax
                
                file_read_pos   02h
                ljc     __read_error
                
                or      ax, ax
                jnz     __continue

                mov     ax, 512
                
        __continue:
                mov     bx, ax
                pop     ax dx
                
                add     ax, bx
                adc     dx, 0
                
                push    dx ax
                
                file_read_pos   08h
                jc      __read_error
                
                mov     bx, ax
                
                file_read_pos   16h
                jc      __read_error
                
                add     bx, ax
                shl     bx, 4
                
                pop     ax dx
                
                sub     ax, bx
                sbb     dx, 0
                
                or      dx, dx
                jz      __no_overflow
                
        __overflow:
                mov     [segment_max], 0FFFFh
                jmp     __success
        
        __no_overflow:
                mov     [segment_max], ax
                
        __success:
                clc
                jmp     __end
                
        __read_error:
                stc
                mov     [segment_max], 0
                
        __end:
                pop     dx bx ax
                ret
        endp
;------------------------------------------------------------------------------
; MAIN PROCEDURE
;------------------------------------------------------------------------------

main:
        init_data_segment
        
        call    parse_params
        
        cmp     byte ptr [file_name], 0
        lje     __display_help
        
        cmp     word ptr [file_name], '?/'
        lje     __display_help
        
        open_file       file_name
        ljc     __file_open_error
        
        mov     [file_handle], ax
        
        ; read file header
        file_read_pos   0h
        ljc     __file_read_error
        
        cmp     ax, 'ZM'                        ; exe file magic number
        jne     __file_not_exe
        
        call    get_code_size
        
        ; finding out code segment start
        xor     dx, dx
        file_read_pos   8h                      ; number of paragraphs in the header
        ljc     __file_read_error
        
        add     dx, ax
        file_read_pos   16h                     ; initial value of CS register
        jc      __file_read_error
        
        add     dx, ax
        shl     dx, 4                           ; dx *= 16 (paragraph size)
        
        ; dx - cs start
        xor     cx, cx
        call    file_seek
        jc      __file_read_error
        
        ; go ahead and disassemble!
        call    disassemble
        
        jmp     __end

__file_not_exe:
        print_error     'The specified file is not an executable.'
        jmp     __end
        
__display_help:
        print_error     'Usage: d PROGRAM.EXE'
        jmp     __end
        
__file_open_error:
        print_error     'Could not open the file.'
        jmp     __end
        
__file_read_error:
        print_error     'Error reading from file.'

__end:
        exit 0
end main
;==============================================================================
