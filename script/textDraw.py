import numpy as np
from PIL import Image, ImageDraw, ImageFont

def create_text_image_with_outline_shadow_bg(text,
                                             font_path,
                                             font_size,
                                             color,
                                             outline_color,
                                             shadow_color,
                                             bg_color,
                                             lineSpace,
                                             wordSpace,
                                             align,
                                             italic,
                                             underline,
                                             gradientColor):
    """
    :param text: 文本内容
    :param font_path: 字体文件路径
    :param font_size: 字体大小
    :param color: 字体颜色
    :param outline_color: 描边颜色，外描边
    :param shadow_color: 阴影颜色
    :param bg_color: 背景色
    :param lineSpace: 行间距
    :param wordSpace: 字间距
    :param align: 对齐方式
    :param italic: 是否斜体
    :param underline: 是否下划线
    :param gradientColor: 渐变色
    :return:
    """

    # 初始化所有内容
    outline_width = round(font_size * 0.03)
    shadow_offset = (int(font_size * 0.1), int(font_size* 0.1))
    shadow_x = shadow_offset[0]
    shadow_y = shadow_offset[1]

    # 根据宽度，计算实际的高度
    font = ImageFont.truetype(font_path, font_size)
    max_box_width = None

    # 计算最大的字，并保证宽度的最小值是这个值
    for char in text:
        box = font.getbbox(char)
        if max_box_width is None or (box[2] - box[0]) > max_box_width:
            max_box_width = box[2] - box[0]

    single_bbox_width = max_box_width

    # 计算倾斜相关，需要预留一定的空间
    skew_factor = 20 / 90.0

    italic_width = 0
    if italic:
        italic_width = int(font_size * skew_factor * 0.8)

    lineSpace = 0 if lineSpace is None else (lineSpace - font_size)
    wordSpace = 0 if wordSpace is None else wordSpace
    align = 'left' if align is None else align

    final_width = 0
    for line in text.splitlines():
        lineWidth = 0
        for char in line:
            box = font.getbbox(char)
            lineWidth += box[2] - box[0]
        lineWidth += wordSpace * (len(line) - 1)

        if final_width < lineWidth:
            final_width = lineWidth
    final_width += italic_width

    # 将内容分行
    lineNums = []

    for line in text.splitlines():
        init_width = 0
        line_content = []

        if len(line) == 0:
            #说明是空行
            lineNums.append("")
            continue

        for char in line:

            box = font.getbbox(char)
            box_width = box[2] - box[0]

            if box_width > font_size:
                final_width += box_width - font_size
            init_width += box_width

            if init_width <= final_width:
                init_width += wordSpace
                line_content.append(char)
                continue
            # 需要自动换行
            lineNums.append(str().join(line_content))
            line_content.clear()

            line_content.append(char)
            init_width = box_width

        if len(line_content) > 0:
            # 这一小行中最小的一行
            lineNums.append(str().join(line_content))
            line_content.clear()

    final_height = 0
    for line in lineNums:
        if len(line) == 0:
            final_height += font_size
        else:
            final_height += round((font.getbbox(line)[3] - font.getbbox(line)[1]))
    final_height += (len(lineNums) - 1) * lineSpace
    if underline:
        final_height += (len(lineNums)) * outline_width * 3


    final_image = Image.new('RGBA', (final_width, final_height))
    bg_image = Image.new('RGBA', (final_width, final_height), color=bg_color)

    mask = bg_image.split()[3]
    final_image.paste(bg_image, (0, 0), mask)

    height = 0
    for lineIndex, line in enumerate(lineNums):
        if len(line) == 0:
            # 是空行
            height += font_size + lineSpace
            continue

        lineBox = font.getbbox(line)
        width_in_fact = lineBox[2] - lineBox[0] + (len(line) - 1) * wordSpace
        lineBox_height = lineBox[3] - lineBox[1]
        single_bbox_height = lineBox_height
        if underline:
            single_bbox_height += 3*outline_width

        # y = 0 if lineIndex == 0 else (single_bbox_height + lineSpace) * lineIndex
        if align == 'left':
            if italic:
                position = (italic_width, height)
            else:
                position = (0, height)
        elif align == 'right':
            position = (final_width - width_in_fact, height)
        elif align == 'center':
            if italic:
                position = [max((final_width - width_in_fact) // 2, italic_width), height]
            else:
                position = ((final_width - width_in_fact) // 2, height)

        linePosition = [position[0], position[1]]
        # 为了保证斜体效果正常，每行一个image
        if gradientColor:
            # 如果是要求字体渐变色，则需要字体作为mask，同时字体颜色值应该设置255
            lineImage = Image.new('L', (final_width, single_bbox_height), 0)
            color = 255
        else:
            lineImage = Image.new('RGBA', (final_width, single_bbox_height), (0, 0, 0, 0))

        lineDraw = ImageDraw.Draw(lineImage)

        if underline:
            lineDraw.line([(position[0], lineBox_height + outline_width),
                           (position[0] + width_in_fact, lineBox_height + outline_width)],
                          fill=color,
                          width=outline_width,
                          joint="curve")
            print(f"下划线的起点 {position[0]}, 重点：{position[0] + width_in_fact}, 行内容：{line}, 字数：{len(line)}")

        yoffset = - lineBox[1]
        # 开始绘制字体
        for wordIndex, word in enumerate(line):
            # 开始描绘文字, 首先计算起始字的位置
            if wordIndex == 0:
                linePosition[0] = position[0]

            wordBox = font.getbbox(word)

            if shadow_color is not None:
                lineDraw.text((linePosition[0] + shadow_x, yoffset + shadow_y), word, font=font, fill=shadow_color)

            if outline_color is not None:
                # 需要描边
                lineDraw.text((linePosition[0], yoffset), word, font=font, fill=color, stroke_width=outline_width,
                              stroke_fill=outline_color)
            else:
                lineDraw.text((linePosition[0], yoffset), word, font=font, fill=color)

            linePosition[0] += wordBox[2] - wordBox[0] + wordSpace

        if gradientColor:
            # 设置了渐变色，需要对文字进行渐变色处理, 实现从左向右渐变
            startColor = hex_to_rgb(gradientColor['stops'][0]['color'])
            endColor = hex_to_rgb(gradientColor['stops'][1]['color'])
            angle = gradientColor['angle'] -90

            gradient = common.create_gradient(final_width, single_bbox_height, angle, startColor, endColor)

            textImage = Image.new('RGBA', (final_width, single_bbox_height), (255, 255, 255, 0))
            textImage.paste(gradient, (0, 0), lineImage)
            lineImage = textImage

        if italic:
            # 需要将字体倾斜, 应用仿射变换
            lineImage = lineImage.transform(
                (final_width, single_bbox_height),
                Image.AFFINE,
                (1, skew_factor, 0, 0, 1, 0),
                resample=Image.BICUBIC
            )
        mask = lineImage.split()[3]
        final_image.paste(lineImage, (0, position[1]), mask)
        height += single_bbox_height + lineSpace

    return final_image
