          .text                         
          .globl main                   

          .data                         
          .align 2                      
_Main:                                  # virtual table
          .word 0                       # parent
          .word _STRING0                # class name



          .text                         
_Main_New:                              # function entry
          sw $fp, 0($sp)                
          sw $ra, -4($sp)               
          move $fp, $sp                 
          addiu $sp, $sp, -16           
_L38:                                   
          li    $t0, 4                  
          sw    $t0, 4($sp)             
          jal   _Alloc                  
          move  $t1, $v0                
          la    $t0, _Main              
          sw    $t0, 0($t1)             
          move  $v0, $t1                
          move  $sp, $fp                
          lw    $ra, -4($fp)            
          lw    $fp, 0($fp)             
          jr    $ra                     

_Main.Binky:                            # function entry
          sw $fp, 0($sp)                
          sw $ra, -4($sp)               
          move $fp, $sp                 
          addiu $sp, $sp, -24           
_L39:                                   
          lw    $t3, 12($fp)            
          li    $t2, 0                  
          lw    $t1, -4($t3)            
          slt   $t0, $t2, $t1           
          sw    $t3, 12($fp)            
          sw    $t2, -8($fp)            
          beqz  $t0, _L41               
_L40:                                   
          lw    $t2, -8($fp)            
          li    $t1, 0                  
          slt   $t0, $t2, $t1           
          sw    $t2, -8($fp)            
          beqz  $t0, _L42               
_L41:                                   
          la    $t0, _STRING1           
          sw    $t0, 4($sp)             
          jal   _PrintString            
          jal   _Halt                   
_L42:                                   
          lw    $t3, -8($fp)            
          lw    $t4, 12($fp)            
          lw    $t1, 8($fp)             
          li    $t2, 4                  
          mul   $t0, $t3, $t2           
          add   $t2, $t4, $t0           
          lw    $t0, 0($t2)             
          lw    $t3, -4($t1)            
          slt   $t2, $t0, $t3           
          sw    $t1, 8($fp)             
          sw    $t4, 12($fp)            
          sw    $t0, -12($fp)           
          beqz  $t2, _L44               
_L43:                                   
          lw    $t0, -12($fp)           
          li    $t3, 0                  
          slt   $t2, $t0, $t3           
          sw    $t0, -12($fp)           
          beqz  $t2, _L45               
_L44:                                   
          la    $t1, _STRING1           
          sw    $t1, 4($sp)             
          jal   _PrintString            
          jal   _Halt                   
_L45:                                   
          lw    $t1, -12($fp)           
          lw    $t0, 8($fp)             
          li    $t2, 4                  
          mul   $t3, $t1, $t2           
          add   $t2, $t0, $t3           
          lw    $t1, 0($t2)             
          sw    $t0, 8($fp)             
          move  $v0, $t1                
          move  $sp, $fp                
          lw    $ra, -4($fp)            
          lw    $fp, 0($fp)             
          jr    $ra                     

main:                                   # function entry
          sw $fp, 0($sp)                
          sw $ra, -4($sp)               
          move $fp, $sp                 
          addiu $sp, $sp, -132          
_L46:                                   
          li    $t2, 5                  
          li    $t1, 0                  
          slt   $t0, $t2, $t1           
          sw    $t2, -8($fp)            
          beqz  $t0, _L48               
_L47:                                   
          la    $t0, _STRING2           
          sw    $t0, 4($sp)             
          jal   _PrintString            
          jal   _Halt                   
_L48:                                   
          lw    $t1, -8($fp)            
          li    $t0, 4                  
          mul   $t2, $t0, $t1           
          add   $t3, $t0, $t2           
          sw    $t3, 4($sp)             
          sw    $t3, -12($fp)           
          sw    $t1, -8($fp)            
          sw    $t0, -16($fp)           
          jal   _Alloc                  
          move  $t2, $v0                
          lw    $t3, -12($fp)           
          lw    $t1, -8($fp)            
          lw    $t0, -16($fp)           
          sw    $t1, 0($t2)             
          li    $t1, 0                  
          add   $t2, $t2, $t3           
          sw    $t3, -12($fp)           
          sw    $t2, -20($fp)           
          sw    $t1, -24($fp)           
          sw    $t0, -16($fp)           
_L49:                                   
          lw    $t1, -12($fp)           
          lw    $t0, -16($fp)           
          sub   $t1, $t1, $t0           
          sw    $t1, -12($fp)           
          sw    $t0, -16($fp)           
          beqz  $t1, _L51               
_L50:                                   
          lw    $t2, -24($fp)           
          lw    $t0, -20($fp)           
          lw    $t1, -16($fp)           
          sub   $t0, $t0, $t1           
          sw    $t2, 0($t0)             
          sw    $t0, -20($fp)           
          sw    $t2, -24($fp)           
          sw    $t1, -16($fp)           
          b     _L49                    
_L51:                                   
          lw    $t0, -20($fp)           
          li    $t3, 0                  
          lw    $t2, -4($t0)            
          slt   $t1, $t3, $t2           
          sw    $t3, -32($fp)           
          sw    $t0, -28($fp)           
          beqz  $t1, _L53               
_L52:                                   
          lw    $t0, -32($fp)           
          li    $t2, 0                  
          slt   $t1, $t0, $t2           
          sw    $t0, -32($fp)           
          beqz  $t1, _L54               
_L53:                                   
          la    $t1, _STRING1           
          sw    $t1, 4($sp)             
          jal   _PrintString            
          jal   _Halt                   
_L54:                                   
          lw    $t0, -32($fp)           
          lw    $t3, -28($fp)           
          li    $t2, 4                  
          mul   $t1, $t0, $t2           
          add   $t2, $t3, $t1           
          lw    $t1, 0($t2)             
          li    $t2, 12                 
          li    $t1, 0                  
          slt   $t4, $t2, $t1           
          sw    $t0, -32($fp)           
          sw    $t3, -28($fp)           
          sw    $t2, -36($fp)           
          beqz  $t4, _L56               
_L55:                                   
          la    $t1, _STRING2           
          sw    $t1, 4($sp)             
          jal   _PrintString            
          jal   _Halt                   
_L56:                                   
          lw    $t0, -36($fp)           
          li    $t4, 4                  
          mul   $t1, $t4, $t0           
          add   $t3, $t4, $t1           
          sw    $t3, 4($sp)             
          sw    $t4, -40($fp)           
          sw    $t3, -44($fp)           
          sw    $t0, -36($fp)           
          jal   _Alloc                  
          move  $t2, $v0                
          lw    $t4, -40($fp)           
          lw    $t3, -44($fp)           
          lw    $t0, -36($fp)           
          sw    $t0, 0($t2)             
          li    $t1, 0                  
          add   $t2, $t2, $t3           
          sw    $t4, -40($fp)           
          sw    $t3, -44($fp)           
          sw    $t2, -48($fp)           
          sw    $t1, -52($fp)           
_L57:                                   
          lw    $t1, -44($fp)           
          lw    $t0, -40($fp)           
          sub   $t1, $t1, $t0           
          sw    $t0, -40($fp)           
          sw    $t1, -44($fp)           
          beqz  $t1, _L59               
_L58:                                   
          lw    $t1, -52($fp)           
          lw    $t2, -48($fp)           
          lw    $t0, -40($fp)           
          sub   $t2, $t2, $t0           
          sw    $t1, 0($t2)             
          sw    $t0, -40($fp)           
          sw    $t2, -48($fp)           
          sw    $t1, -52($fp)           
          b     _L57                    
_L59:                                   
          lw    $t2, -48($fp)           
          lw    $t0, -32($fp)           
          lw    $t1, -28($fp)           
          li    $t4, 4                  
          mul   $t3, $t0, $t4           
          add   $t0, $t1, $t3           
          sw    $t2, 0($t0)             
          li    $t3, 10                 
          li    $t2, 0                  
          slt   $t0, $t3, $t2           
          sw    $t1, -28($fp)           
          sw    $t3, -56($fp)           
          beqz  $t0, _L61               
_L60:                                   
          la    $t0, _STRING2           
          sw    $t0, 4($sp)             
          jal   _PrintString            
          jal   _Halt                   
_L61:                                   
          lw    $t1, -56($fp)           
          li    $t2, 4                  
          mul   $t0, $t2, $t1           
          add   $t4, $t2, $t0           
          sw    $t4, 4($sp)             
          sw    $t4, -60($fp)           
          sw    $t1, -56($fp)           
          sw    $t2, -64($fp)           
          jal   _Alloc                  
          move  $t3, $v0                
          lw    $t4, -60($fp)           
          lw    $t1, -56($fp)           
          lw    $t2, -64($fp)           
          sw    $t1, 0($t3)             
          li    $t0, 0                  
          add   $t3, $t3, $t4           
          sw    $t4, -60($fp)           
          sw    $t3, -68($fp)           
          sw    $t0, -72($fp)           
          sw    $t2, -64($fp)           
_L62:                                   
          lw    $t1, -60($fp)           
          lw    $t0, -64($fp)           
          sub   $t1, $t1, $t0           
          sw    $t1, -60($fp)           
          sw    $t0, -64($fp)           
          beqz  $t1, _L64               
_L63:                                   
          lw    $t2, -72($fp)           
          lw    $t0, -68($fp)           
          lw    $t1, -64($fp)           
          sub   $t0, $t0, $t1           
          sw    $t2, 0($t0)             
          sw    $t0, -68($fp)           
          sw    $t2, -72($fp)           
          sw    $t1, -64($fp)           
          b     _L62                    
_L64:                                   
          lw    $t0, -68($fp)           
          move  $t1, $t0                
          li    $t4, 0                  
          lw    $t3, -4($t1)            
          slt   $t2, $t4, $t3           
          sw    $t4, -80($fp)           
          sw    $t1, -76($fp)           
          beqz  $t2, _L66               
_L65:                                   
          lw    $t0, -80($fp)           
          li    $t2, 0                  
          slt   $t1, $t0, $t2           
          sw    $t0, -80($fp)           
          beqz  $t1, _L67               
_L66:                                   
          la    $t1, _STRING1           
          sw    $t1, 4($sp)             
          jal   _PrintString            
          jal   _Halt                   
_L67:                                   
          lw    $t0, -80($fp)           
          lw    $t2, -28($fp)           
          lw    $t5, -76($fp)           
          li    $t3, 4                  
          mul   $t1, $t0, $t3           
          add   $t3, $t5, $t1           
          lw    $t1, 0($t3)             
          li    $t6, 4                  
          li    $t4, 5                  
          li    $t1, 3                  
          mul   $t3, $t4, $t1           
          li    $t1, 4                  
          div   $t4, $t3, $t1           
          li    $t3, 2                  
          rem   $t1, $t4, $t3           
          add   $t4, $t6, $t1           
          li    $t3, 4                  
          mul   $t1, $t0, $t3           
          add   $t0, $t5, $t1           
          sw    $t4, 0($t0)             
          li    $t4, 0                  
          lw    $t3, -4($t2)            
          slt   $t1, $t4, $t3           
          sw    $t5, -76($fp)           
          sw    $t4, -84($fp)           
          sw    $t2, -28($fp)           
          beqz  $t1, _L69               
_L68:                                   
          lw    $t2, -84($fp)           
          li    $t1, 0                  
          slt   $t0, $t2, $t1           
          sw    $t2, -84($fp)           
          beqz  $t0, _L70               
_L69:                                   
          la    $t0, _STRING1           
          sw    $t0, 4($sp)             
          jal   _PrintString            
          jal   _Halt                   
_L70:                                   
          lw    $t1, -84($fp)           
          lw    $t2, -28($fp)           
          lw    $t3, -76($fp)           
          li    $t0, 4                  
          mul   $t4, $t1, $t0           
          add   $t0, $t2, $t4           
          lw    $t5, 0($t0)             
          li    $t4, 0                  
          lw    $t1, -4($t3)            
          slt   $t0, $t4, $t1           
          sw    $t5, -88($fp)           
          sw    $t4, -92($fp)           
          sw    $t3, -76($fp)           
          sw    $t2, -28($fp)           
          beqz  $t0, _L72               
_L71:                                   
          lw    $t0, -92($fp)           
          li    $t2, 0                  
          slt   $t1, $t0, $t2           
          sw    $t0, -92($fp)           
          beqz  $t1, _L73               
_L72:                                   
          la    $t1, _STRING1           
          sw    $t1, 4($sp)             
          jal   _PrintString            
          jal   _Halt                   
_L73:                                   
          lw    $t4, -92($fp)           
          lw    $t0, -88($fp)           
          lw    $t1, -76($fp)           
          li    $t2, 4                  
          mul   $t3, $t4, $t2           
          add   $t2, $t1, $t3           
          lw    $t4, 0($t2)             
          lw    $t3, -4($t0)            
          slt   $t2, $t4, $t3           
          sw    $t0, -88($fp)           
          sw    $t1, -76($fp)           
          sw    $t4, -96($fp)           
          beqz  $t2, _L75               
_L74:                                   
          lw    $t0, -96($fp)           
          li    $t2, 0                  
          slt   $t1, $t0, $t2           
          sw    $t0, -96($fp)           
          beqz  $t1, _L76               
_L75:                                   
          la    $t1, _STRING1           
          sw    $t1, 4($sp)             
          jal   _PrintString            
          jal   _Halt                   
_L76:                                   
          lw    $t5, -96($fp)           
          lw    $t0, -88($fp)           
          lw    $t4, -76($fp)           
          li    $t2, 4                  
          mul   $t1, $t5, $t2           
          add   $t2, $t0, $t1           
          lw    $t1, 0($t2)             
          li    $t3, 55                 
          li    $t1, 4                  
          mul   $t2, $t5, $t1           
          add   $t1, $t0, $t2           
          sw    $t3, 0($t1)             
          li    $t3, 0                  
          lw    $t2, -4($t4)            
          slt   $t1, $t3, $t2           
          sw    $t4, -76($fp)           
          sw    $t3, -100($fp)          
          beqz  $t1, _L78               
_L77:                                   
          lw    $t0, -100($fp)          
          li    $t2, 0                  
          slt   $t1, $t0, $t2           
          sw    $t0, -100($fp)          
          beqz  $t1, _L79               
_L78:                                   
          la    $t1, _STRING1           
          sw    $t1, 4($sp)             
          jal   _PrintString            
          jal   _Halt                   
_L79:                                   
          lw    $t2, -100($fp)          
          lw    $t1, -28($fp)           
          lw    $t0, -76($fp)           
          li    $t3, 4                  
          mul   $t4, $t2, $t3           
          add   $t3, $t0, $t4           
          lw    $t2, 0($t3)             
          sw    $t2, 4($sp)             
          sw    $t0, -76($fp)           
          sw    $t1, -28($fp)           
          jal   _PrintInt               
          lw    $t0, -76($fp)           
          lw    $t1, -28($fp)           
          la    $t2, _STRING3           
          sw    $t2, 4($sp)             
          sw    $t0, -76($fp)           
          sw    $t1, -28($fp)           
          jal   _PrintString            
          lw    $t0, -76($fp)           
          lw    $t1, -28($fp)           
          li    $t6, 2                  
          li    $t5, 100                
          li    $t4, 0                  
          lw    $t3, -4($t1)            
          slt   $t2, $t4, $t3           
          sw    $t6, -104($fp)          
          sw    $t5, -108($fp)          
          sw    $t4, -112($fp)          
          sw    $t0, -76($fp)           
          sw    $t1, -28($fp)           
          beqz  $t2, _L81               
_L80:                                   
          lw    $t0, -112($fp)          
          li    $t2, 0                  
          slt   $t1, $t0, $t2           
          sw    $t0, -112($fp)          
          beqz  $t1, _L82               
_L81:                                   
          la    $t1, _STRING1           
          sw    $t1, 4($sp)             
          jal   _PrintString            
          jal   _Halt                   
_L82:                                   
          lw    $t5, -112($fp)          
          lw    $t6, -108($fp)          
          lw    $t0, -104($fp)          
          lw    $t1, -28($fp)           
          lw    $t2, -76($fp)           
          li    $t3, 4                  
          mul   $t4, $t5, $t3           
          add   $t3, $t1, $t4           
          lw    $t1, 0($t3)             
          sw    $t6, 4($sp)             
          sw    $t1, 8($sp)             
          sw    $t2, 12($sp)            
          sw    $t0, -104($fp)          
          jal   _Main.Binky             
          move  $t2, $v0                
          lw    $t0, -104($fp)          
          mul   $t1, $t0, $t2           
          sw    $t1, 4($sp)             
          jal   _PrintInt               
          move  $sp, $fp                
          lw    $ra, -4($fp)            
          lw    $fp, 0($fp)             
          jr    $ra                     




          .data                         
_STRING3:
          .asciiz " "                   
_STRING2:
          .asciiz "Decaf runtime error: Cannot create negative-sized array\n"
_STRING0:
          .asciiz "Main"                
_STRING1:
          .asciiz "Decaf runtime error: Array subscript out of bounds\n"
