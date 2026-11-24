# BLDC fan IR codes (extracted from REMOTE_BLDC_FAN v14.0)

All codes: NEC, 38 kHz carrier, LSB first. Full timing arrays and Pronto hex are in fan_ir_codes.json; a Flipper Zero file is fan_ir_codes.ir.

## Atomberg

_Table identified by: brand string match._

| Button | Protocol | Address | Command | NEC bytes | Notes |
|---|---|---|---|---|---|
| Power | NECext | 00 F3 | 91 | `00 F3 91 6E` |  |
| Speed 1 | NECext | 00 F3 | 8B | `00 F3 8B 74` |  |
| Speed 2 | NECext | 00 F3 | 90 | `00 F3 90 6F` |  |
| Speed 3 | NECext | 00 F3 | 8A | `00 F3 8A 75` |  |
| Speed 4 | NECext | 00 F3 | 93 | `00 F3 93 6C` |  |
| Speed 5 | NECext | 00 F3 | 88 | `00 F3 88 77` |  |
| Boost (B center) | NECext | 00 F3 | 8F | `00 F3 8F 70` |  |
| Timer | NECext | 00 F3 | 96 | `00 F3 96 69` |  |
| LED | NECext | 00 F3 | 97 | `00 F3 97 68` |  |
| Sleep | NECext | 00 F3 | 8E | `00 F3 8E 71` |  |

## Activa

_Table identified by: inferred: fallback list; Activa is the only brand without its own table._

| Button | Protocol | Address | Command | NEC bytes | Notes |
|---|---|---|---|---|---|
| Power | NEC | 00 | 0D | `00 FF 0D F2` |  |
| Nature | NEC | 00 | 43 | `00 FF 43 BC` |  |
| Smart | NEC | 00 | 45 | `00 FF 45 BA` |  |
| Speed 1 | NEC | 00 | 08 | `00 FF 08 F7` |  |
| Speed 2 | NEC | 00 | 09 | `00 FF 09 F6` |  |
| Speed 3 | NEC | 00 | 5A | `00 FF 5A A5` |  |
| Speed 4 | NEC | 00 | 07 | `00 FF 07 F8` |  |
| Speed 5 | NEC | 00 | 42 | `00 FF 42 BD` |  |
| Speed 6 | NECext | D8 7E | 00 | `D8 7E 00 FF` | app has no stored signal; it falls back to address D8 7E, which differs from this remote (00) - likely an app bug, probably won't work |
| Boost | NEC | 00 | 1C | `00 FF 1C E3` |  |
| Light On-Off | NEC | 00 | 44 | `00 FF 44 BB` |  |
| Timer Off | NEC | 00 | 16 | `00 FF 16 E9` |  |
| Reverse | NEC | 00 | 15 | `00 FF 15 EA` |  |
| 2H | NEC | 00 | 46 | `00 FF 46 B9` |  |
| 4H | NEC | 00 | 47 | `00 FF 47 B8` |  |
| 8H | NEC | 00 | 48 | `00 FF 48 B7` |  |

## Orient

_Table identified by: brand string match._

| Button | Protocol | Address | Command | NEC bytes | Notes |
|---|---|---|---|---|---|
| On | NEC | 8C | 00 | `8C 73 00 FF` |  |
| Off | NEC | 8C | 10 | `8C 73 10 EF` |  |
| Speed 1 | NEC | 8C | 04 | `8C 73 04 FB` |  |
| Speed 2 | NEC | 8C | 05 | `8C 73 05 FA` |  |
| Speed 3 | NEC | 8C | 06 | `8C 73 06 F9` |  |
| Speed 4 | NEC | 8C | 07 | `8C 73 07 F8` |  |
| Speed 5 | NEC | 8C | 08 | `8C 73 08 F7` |  |
| Speed (center) | NEC | 8C | 03 | `8C 73 03 FC` |  |
| 2H | NEC | 8C | 15 | `8C 73 15 EA` |  |
| 4H | NEC | 8C | 0E | `8C 73 0E F1` |  |
| 6H | NEC | 8C | 16 | `8C 73 16 E9` |  |
| 8H | NEC | 8C | 11 | `8C 73 11 EE` |  |
| LED | NEC | 8C | 13 | `8C 73 13 EC` |  |
| Boost | NEC | 8C | 0F | `8C 73 0F F0` |  |

## Innoel

_Table identified by: remote body captioned INNOEL._

| Button | Protocol | Address | Command | NEC bytes | Notes |
|---|---|---|---|---|---|
| POWER | NECext | D8 7E | 08 | `D8 7E 08 F7` | app keeps sending NEC repeat frames every 108 ms while held |
| LED | NECext | D8 7E | 07 | `D8 7E 07 F8` |  |
| 1 | NECext | D8 7E | 01 | `D8 7E 01 FE` |  |
| 2 | NECext | D8 7E | 02 | `D8 7E 02 FD` |  |
| 3 | NECext | D8 7E | 03 | `D8 7E 03 FC` |  |
| 4 | NECext | D8 7E | 04 | `D8 7E 04 FB` |  |
| 5 | NECext | D8 7E | 05 | `D8 7E 05 FA` |  |
| 6 | NECext | D8 7E | 15 | `D8 7E 15 EA` |  |
| MAX | NECext | D8 7E | 06 | `D8 7E 06 F9` |  |
| 2H | NECext | D8 7E | 09 | `D8 7E 09 F6` |  |
| 4H | NECext | D8 7E | 10 | `D8 7E 10 EF` |  |
| 8H | NECext | D8 7E | 12 | `D8 7E 12 ED` |  |
| SMT | NECext | D8 7E | 14 | `D8 7E 14 EB` |  |
| REV | NECext | D8 7E | 13 | `D8 7E 13 EC` | sent as a long press: frame + 5 NEC repeat frames |

## Walton

_Table identified by: remote body captioned WALTON._

| Button | Protocol | Address | Command | NEC bytes | Notes |
|---|---|---|---|---|---|
| POWER | NECext | D8 7E | 08 | `D8 7E 08 F7` | app keeps sending NEC repeat frames every 108 ms while held |
| LED | NECext | D8 7E | 07 | `D8 7E 07 F8` |  |
| 1 | NECext | D8 7E | 01 | `D8 7E 01 FE` |  |
| 2 | NECext | D8 7E | 02 | `D8 7E 02 FD` |  |
| 3 | NECext | D8 7E | 03 | `D8 7E 03 FC` |  |
| 4 | NECext | D8 7E | 04 | `D8 7E 04 FB` |  |
| 5 | NECext | D8 7E | 05 | `D8 7E 05 FA` |  |
| 6 | NECext | D8 7E | 15 | `D8 7E 15 EA` |  |
| 7 | NECext | D8 7E | 13 | `D8 7E 13 EC` |  |
| REV | NECext | D8 7E | 12 | `D8 7E 12 ED` |  |
| 2H | NECext | D8 7E | 09 | `D8 7E 09 F6` |  |
| 4H | NECext | D8 7E | 10 | `D8 7E 10 EF` |  |
| 6H | NECext | D8 7E | 00 | `D8 7E 00 FF` | same code as '8H' - looks like an unfinished placeholder in the app; probably not the real signal |
| 8H | NECext | D8 7E | 00 | `D8 7E 00 FF` | same code as '6H' - looks like an unfinished placeholder in the app; probably not the real signal |
| ECO | NECext | D8 7E | 14 | `D8 7E 14 EB` |  |

