from PIL import Image,ImageFilter
import numpy as np

def create_gradient(width, height, angle, color1, color2):
    # Create an empty image with RGBA mode
    image = Image.new("RGBA", (width, height))
    pixels = image.load()

    angle_rad = np.radians(angle)

    dx = np.cos(angle_rad)
    dy = np.sin(angle_rad)

    # Calculate the length of the gradient line
    projections = [
        (0 * dx + 0 * dy),
        (width * dx + 0 * dy),
        (0 * dx + height * dy),
        (width * dx + height * dy)
    ]

    min_proj = min(projections)
    max_proj = max(projections)

    for x in range(width):
        for y in range(height):
            # Calculate the position along the gradient line
            position = (x * dx + y * dy)
            position = (position - min_proj) / (max_proj - min_proj)

            # r = int(color1[0] + (color2[0] - color1[0]) * position)
            # g = int(color1[1] + (color2[1] - color1[1]) * position)
            # b = int(color1[2] + (color2[2] - color1[2]) * position)
            r = int(np.interp(position, [0, 1], [color1[0], color2[0]]))
            g = int(np.interp(position, [0, 1], [color1[1], color2[1]]))
            b = int(np.interp(position, [0, 1], [color1[2], color2[2]]))
            pixels[x, y] = (r, g, b) + (255,)

    image = image.filter(ImageFilter.GaussianBlur(radius=500))
    return image

def readJsonValue(object, key, defaultValue):
    if key in object and object[key]:
        return object[key]
    else:
        return defaultValue
