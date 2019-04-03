VTABLE(_A) {
    <empty>
    A
    _A.seta;
    _A.printA;
}

VTABLE(_Main) {
    <empty>
    Main
}

FUNCTION(_A_New) {
memo ''
_A_New:
    _T3 = 8
    parm _T3
    _T4 =  call _Alloc
    _T5 = 0
    *(_T4 + 4) = _T5
    _T6 = VTBL <_A>
    *(_T4 + 0) = _T6
    return _T4
}

FUNCTION(_Main_New) {
memo ''
_Main_New:
    _T7 = 4
    parm _T7
    _T8 =  call _Alloc
    _T9 = VTBL <_Main>
    *(_T8 + 0) = _T9
    return _T8
}

FUNCTION(_A.seta) {
memo '_T0:4 _T1:8'
_A.seta:
    _T10 = *(_T0 + 4)
    *(_T0 + 4) = _T1
}

FUNCTION(_A.printA) {
memo '_T2:4'
_A.printA:
    _T11 = *(_T2 + 4)
    parm _T11
    call _PrintInt
    _T12 = "\n"
    parm _T12
    call _PrintString
}

FUNCTION(main) {
memo ''
main:
    _T15 =  call _A_New
    _T14 = _T15
    _T16 = 10
    parm _T14
    parm _T16
    _T17 = *(_T14 + 0)
    _T18 = *(_T17 + 8)
    call _T18
    _T19 = 6
    _T20 = 0
    _T21 = (_T19 < _T20)
    if (_T21 == 0) branch _L13
    _T22 = "Decaf runtime error: The length of the created array should not be less than 0.\n"
    parm _T22
    call _PrintString
    call _Halt
_L13:
    _T23 = 4
    _T24 = (_T23 * _T19)
    _T25 = (_T23 + _T24)
    parm _T25
    _T26 =  call _Alloc
    *(_T26 + 0) = _T19
    _T27 = 0
    _T26 = (_T26 + _T25)
_L14:
    _T25 = (_T25 - _T23)
    if (_T25 == 0) branch _L15
    _T26 = (_T26 - _T23)
    _T28 = 8
    parm _T28
    _T29 =  call _Alloc
    _T30 = *(_T14 + 4)
    *(_T29 + 4) = _T30
    _T31 = VTBL <_A>
    *(_T29 + 0) = _T31
    _T27 = _T29
    *(_T26 + 0) = _T27
    branch _L14
_L15:
    _T13 = _T26
    _T32 = 1
    _T33 = *(_T13 - 4)
    _T34 = (_T32 < _T33)
    if (_T34 == 0) branch _L16
    _T35 = 0
    _T36 = (_T32 < _T35)
    if (_T36 == 0) branch _L17
_L16:
    _T37 = "Decaf runtime error: Array subscript out of bounds\n"
    parm _T37
    call _PrintString
    call _Halt
_L17:
    _T38 = 4
    _T39 = (_T32 * _T38)
    _T40 = (_T13 + _T39)
    _T41 = *(_T40 + 0)
    _T42 = 15
    parm _T41
    parm _T42
    _T43 = *(_T41 + 0)
    _T44 = *(_T43 + 8)
    call _T44
    _T45 = 0
    _T46 = *(_T13 - 4)
    _T47 = (_T45 < _T46)
    if (_T47 == 0) branch _L18
    _T48 = 0
    _T49 = (_T45 < _T48)
    if (_T49 == 0) branch _L19
_L18:
    _T50 = "Decaf runtime error: Array subscript out of bounds\n"
    parm _T50
    call _PrintString
    call _Halt
_L19:
    _T51 = 4
    _T52 = (_T45 * _T51)
    _T53 = (_T13 + _T52)
    _T54 = *(_T53 + 0)
    parm _T54
    _T55 = *(_T54 + 0)
    _T56 = *(_T55 + 12)
    call _T56
    _T57 = 1
    _T58 = *(_T13 - 4)
    _T59 = (_T57 < _T58)
    if (_T59 == 0) branch _L20
    _T60 = 0
    _T61 = (_T57 < _T60)
    if (_T61 == 0) branch _L21
_L20:
    _T62 = "Decaf runtime error: Array subscript out of bounds\n"
    parm _T62
    call _PrintString
    call _Halt
_L21:
    _T63 = 4
    _T64 = (_T57 * _T63)
    _T65 = (_T13 + _T64)
    _T66 = *(_T65 + 0)
    parm _T66
    _T67 = *(_T66 + 0)
    _T68 = *(_T67 + 12)
    call _T68
}

