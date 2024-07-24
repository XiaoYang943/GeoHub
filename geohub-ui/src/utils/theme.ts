// 处理主题样式
export function handleThemeStyle(theme: string) {
    document.documentElement.style.setProperty('--el-color-primary', theme);
    for (let i = 1; i <= 9; i++) {
        document.documentElement.style.setProperty(`--el-color-primary-light-${i}`, getLightColor(theme, i / 10));
    }
    for (let i = 1; i <= 9; i++) {
        document.documentElement.style.setProperty(`--el-color-primary-dark-${i}`, getDarkColor(theme, i / 10));
    }
}

// hex颜色转rgb颜色
export function hexToRgb(str: string): number[] | null {
    str = str.replace('#', '');
    const hexs = str.match(/../g);
    if (!hexs) {
        return null; // 如果没有匹配项，返回null
    }
    const rgb = [];
    for (let i = 0; i < 3; i++) {
        const hex = parseInt(hexs[i], 16);
        if (isNaN(hex)) {
            return null; // 如果解析失败，返回null
        }
        rgb.push(hex);
    }
    return rgb;
}

// rgb颜色转Hex颜色
export function rgbToHex(r: number, g: number, b: number): string {
    const hexs = [r.toString(16), g.toString(16), b.toString(16)];
    for (let i = 0; i < 3; i++) {
        if (hexs[i].length === 1) {
            hexs[i] = `0${hexs[i]}`;
        }
    }
    return `#${hexs.join('')}`;
}

// 变浅颜色值
export function getLightColor(color: string, level: number): string | null {
    const rgb = hexToRgb(color);
    if (!rgb) {
        return null; // 如果hexToRgb返回null，则无法计算
    }
    for (let i = 0; i < 3; i++) {
        rgb[i] = Math.floor((255 - rgb[i]) * level + rgb[i]);
    }
    return rgbToHex(rgb[0], rgb[1], rgb[2]);
}

// 变深颜色值
export function getDarkColor(color: string, level: number): string | null {
    const rgb = hexToRgb(color);
    if (!rgb) {
        return null; // 如果hexToRgb返回null，则无法计算
    }
    for (let i = 0; i < 3; i++) {
        rgb[i] = Math.floor(rgb[i] * (1 - level));
    }
    return rgbToHex(rgb[0], rgb[1], rgb[2]);
}