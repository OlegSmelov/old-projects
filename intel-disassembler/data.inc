;------------------------------------------------------------------------------
; 16 bit disassembler for DOS
; by Oleg Smelov, 2011
;
; data.inc: data and opcode information
;------------------------------------------------------------------------------

;------------------------------------------------------------------------------
; VARIOUS STRINGS
;------------------------------------------------------------------------------

        str_tab                 db '    ', 0
        str_comma               db ', ', 0
        str_endl                db 13, 10, 0
        str_semicolon           db ': ', 0
        str_brs                 db '[', 0
        str_brc                 db ']', 0
        str_one                 db '1', 0
        str_unknown             db 'Unknown opcode ', 0
        
        str_word_ptr            db 'word ptr ', 0
        str_byte_ptr            db 'byte ptr ', 0

;------------------------------------------------------------------------------
; COMMANDS
;------------------------------------------------------------------------------

        c_unknown               db '?', 0
        
        c_aaa                   db 'aaa', 0
        c_aas                   db 'aas', 0
        c_adc                   db 'adc', 0
        c_add                   db 'add', 0
        c_and                   db 'and', 0
        c_call                  db 'call', 0
        c_cbw                   db 'cbw', 0
        c_clc                   db 'clc', 0
        c_cld                   db 'cld', 0
        c_cli                   db 'cli', 0
        c_cmp                   db 'cmp', 0
        c_cwd                   db 'cwd', 0
        c_daa                   db 'daa', 0
        c_das                   db 'das', 0
        c_dec                   db 'dec', 0
        c_div                   db 'div', 0
        c_idiv                  db 'idiv', 0
        c_imul                  db 'imul', 0
        c_inc                   db 'inc', 0
        c_int                   db 'int', 0
        c_jcxz                  db 'jcxz', 0
        c_jmp                   db 'jmp', 0
        c_lds                   db 'lds', 0
        c_lea                   db 'lea', 0
        c_les                   db 'les', 0
        c_loop                  db 'loop', 0
        c_loopnz                db 'loopnz', 0
        c_loopz                 db 'loopz', 0
        c_mov                   db 'mov', 0
        c_mul                   db 'mul', 0
        c_neg                   db 'neg', 0
        c_nop                   db 'nop', 0
        c_not                   db 'not', 0
        c_or                    db 'or', 0
        c_pop                   db 'pop', 0
        c_push                  db 'push', 0
        c_rcl                   db 'rcl', 0
        c_rcr                   db 'rcr', 0
        c_ret                   db 'ret', 0
        c_rol                   db 'rol', 0
        c_ror                   db 'ror', 0
        c_sal                   db 'sal', 0
        c_sar                   db 'sar', 0
        c_sbb                   db 'sbb', 0
        c_shl                   db 'shl', 0
        c_shr                   db 'shr', 0
        c_stc                   db 'stc', 0
        c_std                   db 'std', 0
        c_sti                   db 'sti', 0
        c_sub                   db 'sub', 0
        c_test                  db 'test', 0
        c_xchg                  db 'xchg', 0
        c_xor                   db 'xor', 0
        
;------------------------------------------------------------------------------
; GROUPS
;------------------------------------------------------------------------------

        grp_shift               dw c_rol, c_ror, c_rcl, c_rcr, c_shl, c_shr, \
                                   c_sal, c_sar
        grp_arithm              dw c_add, c_or, c_adc, c_sbb, c_and, c_sub, \
                                   c_xor, c_cmp
        grp_calljmp             dw c_inc, c_dec, c_call, c_call, c_jmp, c_jmp, \
                                   c_push, c_unknown
        grp_test                dw c_test, c_test, c_not, c_neg, c_mul, c_imul, \
                                   c_div, c_idiv
        grp_incdec              dw c_inc, c_dec, c_unknown, c_unknown, c_unknown, \
                                   c_unknown, c_unknown, c_unknown

;------------------------------------------------------------------------------
; JUMPS
;------------------------------------------------------------------------------

        jump_jo                 db 'jo', 0
        jump_jno                db 'jno', 0
        jump_jb                 db 'jb', 0
        jump_jnb                db 'jnb', 0
        jump_je                 db 'je', 0
        jump_jne                db 'jne', 0
        jump_jbe                db 'jbe', 0
        jump_ja                 db 'ja', 0
        jump_js                 db 'js', 0
        jump_jns                db 'jns', 0
        jump_jp                 db 'jp', 0
        jump_jnp                db 'jnp', 0
        jump_jl                 db 'jl', 0
        jump_jnl                db 'jnl', 0
        jump_jle                db 'jle', 0
        jump_jg                 db 'jg', 0

;------------------------------------------------------------------------------
; BASE CONVERSION
;------------------------------------------------------------------------------

        b_numbers               db '0123456789ABCDEF'

;------------------------------------------------------------------------------
; DATA
;------------------------------------------------------------------------------

        reg16_ax                db 'ax', 0
        reg16_cx                db 'cx', 0
        reg16_dx                db 'dx', 0
        reg16_bx                db 'bx', 0
        reg16_sp                db 'sp', 0
        reg16_bp                db 'bp', 0
        reg16_si                db 'si', 0
        reg16_di                db 'di', 0

        regs16                  dw reg16_ax, reg16_cx, reg16_dx, reg16_bx, \
                                   reg16_sp, reg16_bp, reg16_si, reg16_di
        
        reg8_al                 db 'al', 0
        reg8_cl                 db 'cl', 0
        reg8_dl                 db 'dl', 0
        reg8_bl                 db 'bl', 0
        reg8_ah                 db 'ah', 0
        reg8_ch                 db 'ch', 0
        reg8_dh                 db 'dh', 0
        reg8_bh                 db 'bh', 0

        regs8                   dw reg8_al, reg8_cl, reg8_dl, reg8_bl, \
                                   reg8_ah, reg8_ch, reg8_dh, reg8_bh
        
        seg_es                  db 'es', 0
        seg_cs                  db 'cs', 0
        seg_ss                  db 'ss', 0
        seg_ds                  db 'ds', 0
        
        segs                    dw seg_es, seg_cs, seg_ss, seg_ds
        
        addr_bx_si              db 'bx+si', 0
        addr_bx_di              db 'bx+di', 0
        addr_bp_si              db 'bp+si', 0
        addr_bp_di              db 'bp+di', 0
        addr_si                 db 'si', 0
        addr_di                 db 'di', 0
        addr_bp                 db 'bp', 0
        addr_bx                 db 'bx', 0
        
        addrs                   dw addr_bx_si, addr_bx_di, addr_bp_si, \
                                   addr_bp_di, addr_si, addr_di, addr_bp, \
                                   addr_bx
;------------------------------------------------------------------------------
; OPCODE TYPES
;------------------------------------------------------------------------------

        op_type_unknown         equ 0           ; unknown/unused
        op_type_short           equ 1           ; 1 byte opcode
        op_type_normal          equ 2           ; 2 byte opcode
        op_type_group           equ 3           ; 2 byte opcode (additional opcode)
        op_type_seg_over        equ 4           ; 1 byte (segment override)

;------------------------------------------------------------------------------
; OPERAND TYPES
;------------------------------------------------------------------------------
        
        o_none                  equ 0h          ; no operand
        o_add_reg8              equ 1h          ; last 3 bits - reg8
        o_add_reg16             equ 2h          ; last 3 bits - reg16
        o_imm8                  equ 3h
        o_imm16                 equ 4h
        o_rm8                   equ 5h
        o_rm16                  equ 6h
        o_sreg                  equ 7h
        o_reg8                  equ 8h
        o_reg16                 equ 9h
        o_add_sreg              equ 0Ah         ; bits 3-4 of opcode - cseg
        o_rel8                  equ 0Bh
        o_rel16                 equ 0Ch
        o_moffs8                equ 0Dh         ; like imm8, but mem addr
        o_moffs16               equ 0Eh
        o_imm8_se               equ 0Fh
        
        o_al                    equ 10h
        o_ax                    equ 11h
        o_cl                    equ 12h
        o_one                   equ 13h
        
;------------------------------------------------------------------------------
; OPCODE TABLE
;------------------------------------------------------------------------------

        ;                  TYPE                 GROUP/OPCODE    OP1             OP2
        opcode_table    dw op_type_normal,      c_add,          o_rm8,          o_reg8
        opcode_01       dw op_type_normal,      c_add,          o_rm16,         o_reg16
        opcode_02       dw op_type_normal,      c_add,          o_reg8,         o_rm8
        opcode_03       dw op_type_normal,      c_add,          o_reg16,        o_rm16
        opcode_04       dw op_type_short,       c_add,          o_al,           o_imm8
        opcode_05       dw op_type_short,       c_add,          o_ax,           o_imm16
        opcode_06       dw op_type_short,       c_push,         o_add_sreg,     o_none
        opcode_07       dw op_type_short,       c_pop,          o_add_sreg,     o_none
        opcode_08       dw op_type_normal,      c_or,           o_rm8,          o_reg8
        opcode_09       dw op_type_normal,      c_or,           o_rm16,         o_reg16
        opcode_0A       dw op_type_normal,      c_or,           o_reg8,         o_rm8
        opcode_0B       dw op_type_normal,      c_or,           o_reg16,        o_rm16
        opcode_0C       dw op_type_short,       c_or,           o_al,           o_imm8
        opcode_0D       dw op_type_short,       c_or,           o_ax,           o_imm16
        opcode_0E       dw op_type_short,       c_push,         o_add_sreg,     o_none
        opcode_0F       dw op_type_unknown,     c_unknown,      o_none,         o_none  ; two-byte opcode prefix

        opcode_10       dw op_type_normal,      c_adc,          o_rm8,          o_reg8
        opcode_11       dw op_type_normal,      c_adc,          o_rm16,         o_reg16
        opcode_12       dw op_type_normal,      c_adc,          o_reg8,         o_rm8
        opcode_13       dw op_type_normal,      c_adc,          o_reg16,        o_rm16
        opcode_14       dw op_type_short,       c_adc,          o_al,           o_imm8
        opcode_15       dw op_type_short,       c_adc,          o_ax,           o_imm16
        opcode_16       dw op_type_short,       c_push,         o_add_sreg,     o_none
        opcode_17       dw op_type_short,       c_pop,          o_add_sreg,     o_none
        opcode_18       dw op_type_normal,      c_sbb,          o_rm8,          o_reg8
        opcode_19       dw op_type_normal,      c_sbb,          o_rm16,         o_reg16
        opcode_1A       dw op_type_normal,      c_sbb,          o_reg8,         o_rm8
        opcode_1B       dw op_type_normal,      c_sbb,          o_reg16,        o_rm16
        opcode_1C       dw op_type_short,       c_sbb,          o_al,           o_imm8
        opcode_1D       dw op_type_short,       c_sbb,          o_ax,           o_imm16
        opcode_1E       dw op_type_short,       c_push,         o_add_sreg,     o_none
        opcode_1F       dw op_type_short,       c_pop,          o_add_sreg,     o_none

        opcode_20       dw op_type_normal,      c_and,          o_rm8,          o_reg8
        opcode_21       dw op_type_normal,      c_and,          o_rm16,         o_reg16
        opcode_22       dw op_type_normal,      c_and,          o_reg8,         o_rm8
        opcode_23       dw op_type_normal,      c_and,          o_reg16,        o_rm16
        opcode_24       dw op_type_short,       c_and,          o_al,           o_imm8
        opcode_25       dw op_type_short,       c_and,          o_ax,           o_imm16
        opcode_26       dw op_type_seg_over,    c_unknown,      o_none,         o_none  ; ES override prefix
        opcode_27       dw op_type_short,       c_daa,          o_none,         o_none
        opcode_28       dw op_type_normal,      c_sub,          o_rm8,          o_reg8
        opcode_29       dw op_type_normal,      c_sub,          o_rm16,         o_reg16
        opcode_2A       dw op_type_normal,      c_sub,          o_reg8,         o_rm8
        opcode_2B       dw op_type_normal,      c_sub,          o_reg16,        o_rm16
        opcode_2C       dw op_type_short,       c_sub,          o_al,           o_imm8
        opcode_2D       dw op_type_short,       c_sub,          o_ax,           o_imm16
        opcode_2E       dw op_type_seg_over,    c_unknown,      o_none,         o_none  ; CS override prefix
        opcode_2F       dw op_type_short,       c_das,          o_none,         o_none

        opcode_30       dw op_type_normal,      c_xor,          o_rm8,          o_reg8
        opcode_31       dw op_type_normal,      c_xor,          o_rm16,         o_reg16
        opcode_32       dw op_type_normal,      c_xor,          o_reg8,         o_rm8
        opcode_33       dw op_type_normal,      c_xor,          o_reg16,        o_rm16
        opcode_34       dw op_type_short,       c_xor,          o_al,           o_imm8
        opcode_35       dw op_type_short,       c_xor,          o_ax,           o_imm16
        opcode_36       dw op_type_seg_over,    c_unknown,      o_none,         o_none  ; SS override prefix
        opcode_37       dw op_type_short,       c_aaa,          o_none,         o_none
        opcode_38       dw op_type_normal,      c_cmp,          o_rm8,          o_reg8
        opcode_39       dw op_type_normal,      c_cmp,          o_rm16,         o_reg16
        opcode_3A       dw op_type_normal,      c_cmp,          o_reg8,         o_rm8
        opcode_3B       dw op_type_normal,      c_cmp,          o_reg16,        o_rm16
        opcode_3C       dw op_type_short,       c_cmp,          o_al,           o_imm8
        opcode_3D       dw op_type_short,       c_cmp,          o_ax,           o_imm16
        opcode_3E       dw op_type_seg_over,    c_unknown,      o_none,         o_none  ; DS override prefix
        opcode_3F       dw op_type_short,       c_aas,          o_none,         o_none

        opcode_40       dw op_type_short,       c_inc,          o_add_reg16,    o_none
        opcode_41       dw op_type_short,       c_inc,          o_add_reg16,    o_none
        opcode_42       dw op_type_short,       c_inc,          o_add_reg16,    o_none
        opcode_43       dw op_type_short,       c_inc,          o_add_reg16,    o_none
        opcode_44       dw op_type_short,       c_inc,          o_add_reg16,    o_none
        opcode_45       dw op_type_short,       c_inc,          o_add_reg16,    o_none
        opcode_46       dw op_type_short,       c_inc,          o_add_reg16,    o_none
        opcode_47       dw op_type_short,       c_inc,          o_add_reg16,    o_none
        opcode_48       dw op_type_short,       c_dec,          o_add_reg16,    o_none
        opcode_49       dw op_type_short,       c_dec,          o_add_reg16,    o_none
        opcode_4A       dw op_type_short,       c_dec,          o_add_reg16,    o_none
        opcode_4B       dw op_type_short,       c_dec,          o_add_reg16,    o_none
        opcode_4C       dw op_type_short,       c_dec,          o_add_reg16,    o_none
        opcode_4D       dw op_type_short,       c_dec,          o_add_reg16,    o_none
        opcode_4E       dw op_type_short,       c_dec,          o_add_reg16,    o_none
        opcode_4F       dw op_type_short,       c_dec,          o_add_reg16,    o_none

        opcode_50       dw op_type_short,       c_push,         o_add_reg16,    o_none
        opcode_51       dw op_type_short,       c_push,         o_add_reg16,    o_none
        opcode_52       dw op_type_short,       c_push,         o_add_reg16,    o_none
        opcode_53       dw op_type_short,       c_push,         o_add_reg16,    o_none
        opcode_54       dw op_type_short,       c_push,         o_add_reg16,    o_none
        opcode_55       dw op_type_short,       c_push,         o_add_reg16,    o_none
        opcode_56       dw op_type_short,       c_push,         o_add_reg16,    o_none
        opcode_57       dw op_type_short,       c_push,         o_add_reg16,    o_none
        opcode_58       dw op_type_short,       c_pop,          o_add_reg16,    o_none
        opcode_59       dw op_type_short,       c_pop,          o_add_reg16,    o_none
        opcode_5A       dw op_type_short,       c_pop,          o_add_reg16,    o_none
        opcode_5B       dw op_type_short,       c_pop,          o_add_reg16,    o_none
        opcode_5C       dw op_type_short,       c_pop,          o_add_reg16,    o_none
        opcode_5D       dw op_type_short,       c_pop,          o_add_reg16,    o_none
        opcode_5E       dw op_type_short,       c_pop,          o_add_reg16,    o_none
        opcode_5F       dw op_type_short,       c_pop,          o_add_reg16,    o_none

        opcode_60       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_61       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_62       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_63       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_64       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_65       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_66       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_67       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_68       dw op_type_short,       c_push,         o_imm16,        o_none
        opcode_69       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_6A       dw op_type_short,       c_push,         o_imm8,         o_none
        opcode_6B       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_6C       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_6D       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_6E       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_6F       dw op_type_unknown,     c_unknown,      o_none,         o_none

        opcode_70       dw op_type_short,       jump_jo,        o_rel8,         o_none
        opcode_71       dw op_type_short,       jump_jno,       o_rel8,         o_none
        opcode_72       dw op_type_short,       jump_jb,        o_rel8,         o_none
        opcode_73       dw op_type_short,       jump_jnb,       o_rel8,         o_none
        opcode_74       dw op_type_short,       jump_je,        o_rel8,         o_none
        opcode_75       dw op_type_short,       jump_jne,       o_rel8,         o_none
        opcode_76       dw op_type_short,       jump_jbe,       o_rel8,         o_none
        opcode_77       dw op_type_short,       jump_ja,        o_rel8,         o_none
        opcode_78       dw op_type_short,       jump_js,        o_rel8,         o_none
        opcode_79       dw op_type_short,       jump_jns,       o_rel8,         o_none
        opcode_7A       dw op_type_short,       jump_jp,        o_rel8,         o_none
        opcode_7B       dw op_type_short,       jump_jnp,       o_rel8,         o_none
        opcode_7C       dw op_type_short,       jump_jl,        o_rel8,         o_none
        opcode_7D       dw op_type_short,       jump_jnl,       o_rel8,         o_none
        opcode_7E       dw op_type_short,       jump_jle,       o_rel8,         o_none
        opcode_7F       dw op_type_short,       jump_jg,        o_rel8,         o_none

        opcode_80       dw op_type_group,       grp_arithm,     o_rm8,          o_imm8
        opcode_81       dw op_type_group,       grp_arithm,     o_rm16,         o_imm16
        opcode_82       dw op_type_group,       grp_arithm,     o_rm8,          o_imm8
        opcode_83       dw op_type_group,       grp_arithm,     o_rm16,         o_imm8_se       ; sign-extended
        opcode_84       dw op_type_normal,      c_test,         o_rm8,          o_reg8
        opcode_85       dw op_type_normal,      c_test,         o_rm16,         o_reg16
        opcode_86       dw op_type_normal,      c_xchg,         o_reg8,         o_rm8
        opcode_87       dw op_type_normal,      c_xchg,         o_reg16,        o_rm16
        opcode_88       dw op_type_normal,      c_mov,          o_rm8,          o_reg8
        opcode_89       dw op_type_normal,      c_mov,          o_rm16,         o_reg16
        opcode_8A       dw op_type_normal,      c_mov,          o_reg8,         o_rm8
        opcode_8B       dw op_type_normal,      c_mov,          o_reg16,        o_rm16
        opcode_8C       dw op_type_normal,      c_mov,          o_rm16,         o_sreg
        opcode_8D       dw op_type_normal,      c_lea,          o_reg16,        o_rm8
        opcode_8E       dw op_type_normal,      c_mov,          o_sreg,         o_rm16
        opcode_8F       dw op_type_normal,      c_pop,          o_rm16,         o_none

        opcode_90       dw op_type_short,       c_nop,          o_none,         o_none
        opcode_91       dw op_type_short,       c_xchg,         o_add_reg16,    o_ax
        opcode_92       dw op_type_short,       c_xchg,         o_add_reg16,    o_ax
        opcode_93       dw op_type_short,       c_xchg,         o_add_reg16,    o_ax
        opcode_94       dw op_type_short,       c_xchg,         o_add_reg16,    o_ax
        opcode_95       dw op_type_short,       c_xchg,         o_add_reg16,    o_ax
        opcode_96       dw op_type_short,       c_xchg,         o_add_reg16,    o_ax
        opcode_97       dw op_type_short,       c_xchg,         o_add_reg16,    o_ax
        opcode_98       dw op_type_short,       c_cbw,          o_none,         o_none
        opcode_99       dw op_type_short,       c_cwd,          o_none,         o_none
        opcode_9A       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_9B       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_9C       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_9D       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_9E       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_9F       dw op_type_unknown,     c_unknown,      o_none,         o_none

        opcode_A0       dw op_type_short,       c_mov,          o_al,           o_moffs8
        opcode_A1       dw op_type_short,       c_mov,          o_ax,           o_moffs16
        opcode_A2       dw op_type_short,       c_mov,          o_moffs8,       o_al
        opcode_A3       dw op_type_short,       c_mov,          o_moffs16,      o_ax
        opcode_A4       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_A5       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_A6       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_A7       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_A8       dw op_type_short,       c_test,         o_al,           o_imm8
        opcode_A9       dw op_type_short,       c_test,         o_ax,           o_imm16
        opcode_AA       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_AB       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_AC       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_AD       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_AE       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_AF       dw op_type_unknown,     c_unknown,      o_none,         o_none

        opcode_B0       dw op_type_short,       c_mov,          o_add_reg8,     o_imm8
        opcode_B1       dw op_type_short,       c_mov,          o_add_reg8,     o_imm8
        opcode_B2       dw op_type_short,       c_mov,          o_add_reg8,     o_imm8
        opcode_B3       dw op_type_short,       c_mov,          o_add_reg8,     o_imm8
        opcode_B4       dw op_type_short,       c_mov,          o_add_reg8,     o_imm8
        opcode_B5       dw op_type_short,       c_mov,          o_add_reg8,     o_imm8
        opcode_B6       dw op_type_short,       c_mov,          o_add_reg8,     o_imm8
        opcode_B7       dw op_type_short,       c_mov,          o_add_reg8,     o_imm8
        opcode_B8       dw op_type_short,       c_mov,          o_add_reg16,    o_imm16
        opcode_B9       dw op_type_short,       c_mov,          o_add_reg16,    o_imm16
        opcode_BA       dw op_type_short,       c_mov,          o_add_reg16,    o_imm16
        opcode_BB       dw op_type_short,       c_mov,          o_add_reg16,    o_imm16
        opcode_BC       dw op_type_short,       c_mov,          o_add_reg16,    o_imm16
        opcode_BD       dw op_type_short,       c_mov,          o_add_reg16,    o_imm16
        opcode_BE       dw op_type_short,       c_mov,          o_add_reg16,    o_imm16
        opcode_BF       dw op_type_short,       c_mov,          o_add_reg16,    o_imm16

        opcode_C0       dw op_type_group,       grp_shift,      o_rm8,          o_imm8
        opcode_C1       dw op_type_group,       grp_shift,      o_rm16,         o_imm16
        opcode_C2       dw op_type_short,       c_ret,          o_imm16,        o_none
        opcode_C3       dw op_type_short,       c_ret,          o_none,         o_none
        opcode_C4       dw op_type_normal,      c_les,          o_reg16,        o_rm16
        opcode_C5       dw op_type_normal,      c_lds,          o_reg16,        o_rm16
        opcode_C6       dw op_type_normal,      c_mov,          o_rm8,          o_imm8
        opcode_C7       dw op_type_normal,      c_mov,          o_rm16,         o_imm16
        opcode_C8       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_C9       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_CA       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_CB       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_CC       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_CD       dw op_type_short,       c_int,          o_imm8,         o_none
        opcode_CE       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_CF       dw op_type_unknown,     c_unknown,      o_none,         o_none

        opcode_D0       dw op_type_group,       grp_shift,      o_rm8,          o_one
        opcode_D1       dw op_type_group,       grp_shift,      o_rm16,         o_one
        opcode_D2       dw op_type_group,       grp_shift,      o_rm8,          o_cl
        opcode_D3       dw op_type_group,       grp_shift,      o_rm16,         o_cl
        opcode_D4       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_D5       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_D6       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_D7       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_D8       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_D9       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_DA       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_DB       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_DC       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_DD       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_DE       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_DF       dw op_type_unknown,     c_unknown,      o_none,         o_none

        opcode_E0       dw op_type_short,       c_loopnz,       o_rel8,         o_none
        opcode_E1       dw op_type_short,       c_loopz,        o_rel8,         o_none
        opcode_E2       dw op_type_short,       c_loop,         o_rel8,         o_none
        opcode_E3       dw op_type_short,       c_jcxz,         o_rel8,         o_none
        opcode_E4       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_E5       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_E6       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_E7       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_E8       dw op_type_short,       c_call,         o_rel16,        o_none
        opcode_E9       dw op_type_short,       c_jmp,          o_rel16,        o_none
        opcode_EA       dw op_type_unknown,     c_unknown,      o_none,         o_none  ; jmpf?
        opcode_EB       dw op_type_short,       c_jmp,          o_rel8,         o_none
        opcode_EC       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_ED       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_EE       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_EF       dw op_type_unknown,     c_unknown,      o_none,         o_none

        opcode_F0       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_F1       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_F2       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_F3       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_F4       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_F5       dw op_type_unknown,     c_unknown,      o_none,         o_none
        opcode_F6       dw op_type_group,       grp_test,       o_rm8,          o_imm8
        opcode_F7       dw op_type_group,       grp_test,       o_rm16,         o_imm16
        opcode_F8       dw op_type_short,       c_clc,          o_none,         o_none
        opcode_F9       dw op_type_short,       c_stc,          o_none,         o_none
        opcode_FA       dw op_type_short,       c_cli,          o_none,         o_none
        opcode_FB       dw op_type_short,       c_sti,          o_none,         o_none
        opcode_FC       dw op_type_short,       c_cld,          o_none,         o_none
        opcode_FD       dw op_type_short,       c_std,          o_none,         o_none
        opcode_FE       dw op_type_group,       grp_incdec,     o_rm8,          o_none
        opcode_FF       dw op_type_group,       grp_calljmp,    o_rm16,         o_none
                
;==============================================================================