# Extracted BLDC fan IR codes

Source: `REMOTE_BLDC_FAN_v14.0.apk` (a third-party fan remote app), reverse-engineered from its
`classes.dex`. The app knows five brands; **Walton** is the one this project targets.

| File | Contents |
|---|---|
| `fan_ir_codes.md` | Per-brand table: button, protocol, address, command, NEC bytes, caveats |
| `fan_ir_codes.json` | Same, plus exact `ConsumerIrManager` timing arrays (µs) and Pronto hex |
| `fan_ir_codes.ir` | Flipper Zero signal file |
| `extraction/` | Scripts and intermediate data to reproduce the extraction |

## Walton (Super Saver-IR)

NEC **extended** (16-bit) address `D8 7E`, 38 kHz, LSB first. Frame = 8880/4470 leader, 580 µs
marks, 620/1620 µs spaces, 580 µs stop; NEC repeat frame `[8880, 2230, 580]` every 108 ms.

| Button | Command | NEC bytes |
|---|---|---|
| POWER | 08 | `D8 7E 08 F7` (app repeats while held) |
| LED | 07 | `D8 7E 07 F8` |
| Speed 1–5 | 01–05 | `D8 7E 0n ..` |
| Speed 6 | 15 | `D8 7E 15 EA` |
| Speed 7 (MAX) | 13 | `D8 7E 13 EC` |
| REV | 12 | `D8 7E 12 ED` |
| 2H / 4H | 09 / 10 | `D8 7E 09 F6` / `D8 7E 10 EF` |
| ECO | 14 | `D8 7E 14 EB` |
| 6H / 8H | 00 / 00 | **Unreliable**: both are `00` in the app, likely placeholders |

These are built into the app (`FanButton.kt`); 6H/8H are left out. The Arduino capture sketch in
`../capture/` can verify any code.

## How it was extracted
1. `dexdump -d classes.dex` (Android build-tools).
2. `extraction/extract_ir.py dump.txt classes.dex commands.json` resolves every `r.wg(id, label,
   code, repeat, longPressRepeats, rawTiming)` constructor call (69 commands), reading stored raw
   timing arrays straight from the dex.
3. `extraction/build_outputs.py commands.json <out>` reproduces the app's encoders for commands
   without stored timing (`r.Ij.n`: NEC with address `D8 7E`; `r.Ij.d`: frame + N repeat frames)
   and writes the files above. Every stored array was checked to decode to the app's command byte
   with a valid inverse.

Brand mapping: Atomberg and Orient by brand-name checks; Walton and Innoel by the captions on
their remote screens; Activa is inferred (the only brand left for the fallback list).
