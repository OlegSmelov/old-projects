;------------------------------------------------------------------------------
; 16 bit disassembler for DOS
; by Oleg Smelov, 2011
;
; macros.inc
;------------------------------------------------------------------------------

;------------------------------------------------------------------------------
; CONSTANTS
;------------------------------------------------------------------------------

CRLF            equ 13, 10
STDIN           equ 0
STDOUT          equ 1
STDERR          equ 2

;------------------------------------------------------------------------------
; MACROS
;------------------------------------------------------------------------------

init_data_segment macro
        mov     ax, @data
        mov     ds, ax
endm
;------------------------------------------------------------------------------
exit macro exit_code
        mov     ah, 4Ch
        mov     al, exit_code
        int     21h
endm
;------------------------------------------------------------------------------
; "long" jump if equal
lje macro jump_to
local   __label
        jne     __label
        jmp     jump_to
__label:
endm
;------------------------------------------------------------------------------
ljc macro jump_to
local   __label
        jnc     __label
        jmp     jump_to
__label:
endm
;------------------------------------------------------------------------------
ljb macro jump_to
local   __label
        jnb     __label
        jmp     jump_to
__label:
endm
;------------------------------------------------------------------------------
print macro message, file_handle
local   msg, msg_len
.data
        msg             db message, CRLF
        msg_len         equ $ - msg
.code
        push    ax bx cx dx
        
        mov     ah, 40h
        mov     bx, file_handle
        mov     cx, msg_len
        mov     dx, offset msg
        int     21h
        
        pop     dx cx bx ax
endm
;------------------------------------------------------------------------------
print_error macro message
        print   message, STDERR
endm
;------------------------------------------------------------------------------
print_message macro message
        print   message, STDOUT
endm
;------------------------------------------------------------------------------
print_string_reg macro string
        push    string
        call    print_asciz
endm
;------------------------------------------------------------------------------
print_string macro string
        push    offset string
        call    print_asciz
endm
;------------------------------------------------------------------------------
print_char macro char
        push    ax dx
        mov     dl, char
        mov     ah, 2
        int     21h
        pop     dx ax
endm
;------------------------------------------------------------------------------
open_file macro file_name
        push    dx
        mov     ax, 3D00h                       ; open for reading
        mov     dx, offset file_name
        int     21h
        pop     dx
endm
;------------------------------------------------------------------------------
file_read_buffer macro buffer, buffer_size
        push    cx dx
        mov     cx, buffer_size
        mov     dx, offset buffer
        call    file_read
        pop     dx cx
endm
;------------------------------------------------------------------------------
file_read_pos macro pos
local   __end, file_word
.data
        file_word       dw ?
.code
        push    cx dx
        xor     cx, cx
        mov     dx, pos
        call    file_seek
        jc      __end
        
        file_read_buffer        file_word, 2
        jc      __end
        
        mov     ax, [file_word]
__end:
        pop     dx cx
endm
;==============================================================================