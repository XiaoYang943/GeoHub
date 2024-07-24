/**
 * 判断URL是否是http或https
 * @param url 需要判断的URL字符串
 * @returns 如果URL以http://或https://开头，则返回true，否则返回false
 */
export function isHttp(url: string): boolean {
    return url.indexOf('http://') !== -1 || url.indexOf('https://') !== -1;
}

/**
 * 判断path是否为外链
 * @param path 需要判断的字符串
 * @returns 如果path以http://, https://, mailto:, 或tel:开头，则返回true，否则返回false
 */
export function isExternal(path: string): boolean {
    return /^(https?:|mailto:|tel:)/.test(path);
}

/**
 * 验证用户名是否有效
 * @param str 需要验证的用户名字符串
 * @returns 如果用户名是'admin'或'editor'之一（不区分大小写），则返回true，否则返回false
 */
export function validUsername(str: string): boolean {
    const valid_map = ['admin', 'editor'];
    return valid_map.includes(str.trim().toLowerCase()); // 使用includes和toLowerCase确保不区分大小写
}

/**
 * 验证URL是否有效
 * @param url 需要验证的URL字符串
 * @returns 如果URL符合标准格式，则返回true，否则返回false
 */
export function validURL(url: string): boolean {
    const reg = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/;
    return reg.test(url);
}

/**
 * 验证字符串是否全部为小写
 * @param str 需要验证的字符串
 * @returns 如果字符串全部由小写字母组成，则返回true，否则返回false
 */
export function validLowerCase(str: string): boolean {
    const reg = /^[a-z]+$/;
    return reg.test(str);
}

/**
 * 验证字符串是否全部为大写
 * @param str 需要验证的字符串
 * @returns 如果字符串全部由大写字母组成，则返回true，否则返回false
 */
export function validUpperCase(str: string): boolean {
    const reg = /^[A-Z]+$/;
    return reg.test(str);
}

/**
 * 验证字符串是否只包含字母（不区分大小写）
 * @param str 需要验证的字符串
 * @returns 如果字符串只包含字母，则返回true，否则返回false
 */
export function validAlphabets(str: string): boolean {
    const reg = /^[A-Za-z]+$/;
    return reg.test(str);
}

/**
 * 验证电子邮件地址是否有效
 * @param email 需要验证的电子邮件地址
 * @returns 如果电子邮件地址有效，则返回true，否则返回false
 */
export function validEmail(email: string): boolean {
    const reg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return reg.test(email);
}

/**
 * 验证参数是否为字符串
 * @param str 需要验证的参数
 * @returns 如果参数是字符串，则返回true，否则返回false
 */
export function isString(str: any): boolean {
    if (typeof str === 'string' || str instanceof String) {
        return true;
    }
    return false;
}

/**
 * 验证参数是否为数组
 * @param arg 需要验证的参数
 * @returns 如果参数是数组，则返回true，否则返回false
 */
export function isArray(arg: any): boolean {
    if (typeof Array.isArray === 'undefined') {
        return Object.prototype.toString.call(arg) === '[object Array]';
    }
    return Array.isArray(arg);
}
