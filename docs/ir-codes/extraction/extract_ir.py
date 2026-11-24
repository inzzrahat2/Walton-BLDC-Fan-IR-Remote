"""Extract IR command definitions (r.wg constructor calls) from a dexdump -d listing.

Usage: python3 -I extract_ir.py <dump.txt> <classes.dex> <out.json>
Tracks register contents within each method well enough to resolve the constructor
arguments: (id, label, code Integer, repeat Z, e I, raw [I, defaultsMask I).
"""
import json
import re
import struct
import sys

dump_path, dex_path, out_path = sys.argv[1:4]
dex = open(dex_path, "rb").read()

INSN = re.compile(r"^([0-9a-f]{6}): [0-9a-f ]+\|([0-9a-f]{4}): (.*)$")
METHOD = re.compile(r"^[0-9a-f]{6}:\s+\|\[[0-9a-f]+\] (\S+)")
REG = r"(v\d+)"


def read_array_payload(offset):
    ident, width, size = struct.unpack_from("<HHI", dex, offset)
    if ident != 0x0300:
        raise ValueError(f"no array payload at {offset:#x}")
    fmt = {1: "b", 2: "h", 4: "i", 8: "q"}[width]
    return list(struct.unpack_from("<" + fmt * size, dex, offset + 8))


def parse_args(text):
    m = re.search(r"\{([^}]*)\}", text)
    inner = m.group(1).strip()
    if ".." in inner:  # invoke/range shown as "v0 .. v7"
        a, b = [int(x.strip()[1:]) for x in inner.split("..")]
        return [f"v{i}" for i in range(a, b + 1)]
    return [x.strip() for x in inner.split(",") if x.strip()]


results = []
method = None
regs = {}
last_result = None
for line in open(dump_path, encoding="utf-8", errors="replace"):
    line = line.rstrip("\n")
    mm = METHOD.match(line)
    if mm:
        method, regs, last_result = mm.group(1), {}, None
        continue
    m = INSN.match(line)
    if not m:
        continue
    addr = int(m.group(1), 16)
    ins = m.group(3)
    op = ins.split(" ", 1)[0]

    if op.startswith("const-string"):
        r, s = re.match(r"\S+ " + REG + r', "(.*)" //', ins).groups()
        regs[r] = ("str", s)
    elif op.startswith("const-wide"):
        r = re.match(r"\S+ " + REG, ins).group(1)
        regs[r] = ("wide", None)
    elif op.startswith("const"):
        cm = re.match(r"\S+ " + REG + r", .*// #([0-9a-f]+)$", ins)
        if not cm:  # const-class etc.
            r = re.match(r"\S+ " + REG, ins).group(1)
            regs[r] = None
            continue
        r, hexv = cm.groups()
        v = int(hexv, 16)
        if v >= 1 << 31:
            v -= 1 << 32
        regs[r] = ("int", v)
    elif op.startswith("move-result"):
        r = re.match(r"\S+ " + REG, ins).group(1)
        regs[r] = last_result
    elif op.startswith("move-object") or op == "move" or op.startswith("move/"):
        dst, src = re.match(r"\S+ " + REG + ", " + REG, ins).groups()
        regs[dst] = regs.get(src)
    elif op == "new-array":
        r = re.match(r"\S+ " + REG, ins).group(1)
        regs[r] = ("arr", None)
    elif op == "fill-array-data":
        r, rel = re.match(r"\S+ " + REG + r", [0-9a-f]+ // ([+-][0-9a-f]+)", ins).groups()
        regs[r] = ("arr", read_array_payload(addr + 2 * int(rel, 16)))
    elif op.startswith("invoke"):
        args = parse_args(ins)
        vals = [regs.get(a) for a in args]
        last_result = None
        if "Ljava/lang/Integer;.valueOf" in ins:
            last_result = vals[0]
        elif "Lr/wg;.<init>" in ins:
            _, ident, label, code, repeat, e, raw, mask = vals
            results.append({
                "method": method,
                "id": ident[1] if ident and ident[0] == "str" else None,
                "label": label[1] if label and label[0] == "str" else None,
                "code": code[1] if code and code[0] == "int" else None,
                "repeat": bool(repeat[1]) if repeat and repeat[0] == "int" else None,
                "e": e[1] if e and e[0] == "int" else None,
                "raw": raw[1] if raw and raw[0] == "arr" else None,
                "defaults_mask": mask[1] if mask and mask[0] == "int" else None,
            })

json.dump(results, open(out_path, "w"), indent=1)
print(f"{len(results)} commands")
