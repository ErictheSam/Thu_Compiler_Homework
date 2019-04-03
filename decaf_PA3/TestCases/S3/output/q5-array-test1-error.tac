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
    _T6 = 2
    _T4 = _T6
    _T7 = 2
    _T8 = 3
    _T9 = (_T7 - _T8)
    _T5 = _T9
    _T10 = 3
    _T11 = 0
    _T12 = (_T5 < _T11)
    if (_T12 == 0) branch _L10
    _T13 = "Decaf runtime error: The length of the created array should not be less than 0.\n"
    parm _T13
    call _PrintString
    call _Halt
_L10:
    _T14 = 4
    _T15 = (_T14 * _T5)
    _T16 = (_T14 + _T15)
    parm _T16
    _T17 =  call _Alloc
    *(_T17 + 0) = _T5
    _T18 = 0
    _T17 = (_T17 + _T16)
_L11:
    _T16 = (_T16 - _T14)
    if (_T16 == 0) branch _L12
    _T17 = (_T17 - _T14)
    *(_T17 + 0) = _T10
    branch _L11
_L12:
    _T3 = _T17
    _T19 = 1
    _T20 = *(_T3 - 4)
    _T21 = (_T19 < _T20)
    if (_T21 == 0) branch _L13
    _T22 = 0
    _T23 = (_T19 < _T22)
    if (_T23 == 0) branch _L14
_L13:
    _T24 = "Decaf runtime error: Array subscript out of bounds\n"
    parm _T24
    call _PrintString
    call _Halt
_L14:
    _T25 = 4
    _T26 = (_T19 * _T25)
    _T27 = (_T3 + _T26)
    _T28 = *(_T27 + 0)
    _T29 = 1
    _T30 = *(_T3 - 4)
    _T31 = (_T29 < _T30)
    if (_T31 == 0) branch _L15
    _T32 = 0
    _T33 = (_T29 < _T32)
    if (_T33 == 0) branch _L16
_L15:
    _T34 = "Decaf runtime error: Array subscript out of bounds\n"
    parm _T34
    call _PrintString
    call _Halt
_L16:
    _T35 = 4
    _T36 = (_T29 * _T35)
    _T37 = (_T3 + _T36)
    _T38 = *(_T37 + 0)
    _T39 = 1
    _T40 = (_T38 + _T39)
    _T41 = 4
    _T42 = (_T19 * _T41)
    _T43 = (_T3 + _T42)
    *(_T43 + 0) = _T40
    _T44 = 0
    _T45 = *(_T3 - 4)
    _T46 = (_T44 < _T45)
    if (_T46 == 0) branch _L17
    _T47 = 0
    _T48 = (_T44 < _T47)
    if (_T48 == 0) branch _L18
_L17:
    _T49 = "Decaf runtime error: Array subscript out of bounds\n"
    parm _T49
    call _PrintString
    call _Halt
_L18:
    _T50 = 4
    _T51 = (_T44 * _T50)
    _T52 = (_T3 + _T51)
    _T53 = *(_T52 + 0)
    parm _T53
    call _PrintInt
    _T54 = "\n"
    parm _T54
    call _PrintString
    _T55 = 1
    _T56 = *(_T3 - 4)
    _T57 = (_T55 < _T56)
    if (_T57 == 0) branch _L19
    _T58 = 0
    _T59 = (_T55 < _T58)
    if (_T59 == 0) branch _L20
_L19:
    _T60 = "Decaf runtime error: Array subscript out of bounds\n"
    parm _T60
    call _PrintString
    call _Halt
_L20:
    _T61 = 4
    _T62 = (_T55 * _T61)
    _T63 = (_T3 + _T62)
    _T64 = *(_T63 + 0)
    parm _T64
    call _PrintInt
    _T65 = "\n"
    parm _T65
    call _PrintString
}

