VTABLE(_Main) {
    <empty>
    Main
}

FUNCTION(_Main_New) {
memo ''
_Main_New:
    _T0 = 4
    parm _T0
    _T1 =  call _Alloc
    _T2 = VTBL <_Main>
    *(_T1 + 0) = _T2
    return _T1
}

FUNCTION(main) {
memo ''
main:
    _T4 = "testing division by 0 runtime error1\n"
    parm _T4
    call _PrintString
    _T5 = 13
    _T6 = 0
    if (_T6 != 0) branch _L10
    _T8 = "Decaf runtime error: Division by zero error.\n"
    parm _T8
    call _PrintString
    call _Halt
_L10:
    _T7 = (_T5 / _T6)
    _T3 = _T7
    _T9 = "end"
    parm _T9
    call _PrintString
}

