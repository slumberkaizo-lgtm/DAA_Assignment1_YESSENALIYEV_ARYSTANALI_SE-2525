from __future__ import annotations

import csv
import math
from collections import defaultdict
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parent
RESULTS = ROOT / "app" / "results.csv"

COLORS = {
    "MergeSort-random": (27, 94, 163),
    "MergeSort-sorted": (65, 145, 95),
    "MergeSort-duplicates": (178, 116, 37),
    "QuickSort-random": (136, 68, 153),
    "QuickSort-sorted": (197, 76, 61),
    "QuickSort-duplicates": (68, 138, 148),
    "QuickSelect-random": (100, 100, 100),
    "QuickSelect-sorted": (80, 116, 182),
    "QuickSelect-duplicates": (185, 86, 124),
}


def read_rows():
    with RESULTS.open(newline="", encoding="utf-8") as file:
        return list(csv.DictReader(file))


def grouped(rows, value_key):
    data = defaultdict(list)
    for row in rows:
        key = f"{row['algorithm']}-{row['input']}"
        data[key].append((int(row["n"]), float(row[value_key])))
    for values in data.values():
        values.sort()
    return data


def ratio_rows(rows):
    data = defaultdict(list)
    for row in rows:
        n = int(row["n"])
        comparisons = float(row["comparisons"])
        if row["algorithm"] == "QuickSelect":
            ratio = comparisons / n
        else:
            ratio = comparisons / (n * math.log2(n))
        data[f"{row['algorithm']}-{row['input']}"].append((n, ratio))
    for values in data.values():
        values.sort()
    return data


def draw_plot(data, output, title, y_label, log_y=False):
    width, height = 1200, 760
    left, right, top, bottom = 105, 280, 70, 95
    plot_w = width - left - right
    plot_h = height - top - bottom

    image = Image.new("RGB", (width, height), "white")
    draw = ImageDraw.Draw(image)
    font = ImageFont.load_default()
    title_font = ImageFont.load_default(size=22)

    xs = sorted({x for points in data.values() for x, _ in points})
    min_x, max_x = math.log10(xs[0]), math.log10(xs[-1])
    ys = [y for points in data.values() for _, y in points]
    if log_y:
        min_y = math.floor(math.log10(max(min(ys), 1e-9)))
        max_y = math.ceil(math.log10(max(ys)))
    else:
        min_y = 0
        max_y = max(ys) * 1.1 if ys else 1

    def sx(n):
        return left + (math.log10(n) - min_x) / (max_x - min_x) * plot_w

    def sy(value):
        if log_y:
            mapped = (math.log10(max(value, 1e-9)) - min_y) / (max_y - min_y)
        else:
            mapped = (value - min_y) / (max_y - min_y)
        return top + (1 - mapped) * plot_h

    draw.text((left, 28), title, fill=(20, 20, 20), font=title_font)
    draw.line((left, top, left, top + plot_h), fill=(45, 45, 45), width=2)
    draw.line((left, top + plot_h, left + plot_w, top + plot_h), fill=(45, 45, 45), width=2)

    for n in xs:
        x = sx(n)
        draw.line((x, top, x, top + plot_h), fill=(232, 232, 232))
        draw.text((x - 28, top + plot_h + 15), f"{n:,}".replace(",", " "), fill=(40, 40, 40), font=font)

    for i in range(7):
        if log_y:
            value = 10 ** (min_y + (max_y - min_y) * i / 6)
            label = f"{value:.0f}" if value >= 1 else f"{value:.2g}"
        else:
            value = min_y + (max_y - min_y) * i / 6
            label = f"{value:.1f}" if value < 100 else f"{value:.0f}"
        y = sy(value)
        draw.line((left, y, left + plot_w, y), fill=(232, 232, 232))
        draw.text((18, y - 7), label, fill=(40, 40, 40), font=font)

    draw.text((left + plot_w / 2 - 20, height - 38), "n", fill=(20, 20, 20), font=font)
    draw.text((18, 48), y_label, fill=(20, 20, 20), font=font)

    legend_x = left + plot_w + 25
    for idx, (key, points) in enumerate(sorted(data.items())):
        color = COLORS.get(key, (0, 0, 0))
        coords = [(sx(n), sy(v)) for n, v in points]
        for a, b in zip(coords, coords[1:]):
            draw.line((*a, *b), fill=color, width=3)
        for x, y in coords:
            draw.ellipse((x - 4, y - 4, x + 4, y + 4), fill=color)
        y = top + idx * 24
        draw.line((legend_x, y + 7, legend_x + 28, y + 7), fill=color, width=3)
        draw.text((legend_x + 36, y), key, fill=(25, 25, 25), font=font)

    image.save(output)


def main():
    rows = read_rows()
    draw_plot(grouped(rows, "time_ms"), ROOT / "1_time_vs_n.png", "Time vs n", "time_ms", log_y=True)
    draw_plot(grouped(rows, "max_depth"), ROOT / "2_depth_vs_n.png", "Max recursion depth vs n", "max_depth")
    draw_plot(ratio_rows(rows), ROOT / "3_ratio_vs_n.png", "Ratio vs n", "comparisons / expected growth")


if __name__ == "__main__":
    main()
