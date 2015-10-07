var VersionUtil = module.exports;
/**
 * Simply compares two string version values.
 *
 * Example:
 * versionCompare('1.1', '1.2') => -1
 * versionCompare('1.1', '1.1') =>  0
 * versionCompare('1.2', '1.1') =>  1
 * versionCompare('2.23.3', '2.22.3') => 1
 *
 * Returns:
 * -1 = left is LOWER than right
 *  0 = they are equal
 *  1 = left is GREATER = right is LOWER
 *  And FALSE if one of input versions are not valid
 *
 * @function
 * @param {String} left  Version #1
 * @param {String} right Version #2
 * @return {Integer|Boolean}
 * @author Alexey Bass (albass)
 * @since 2011-07-14
 */
VersionUtil.compare = function(left, right) {
    if ( typeof left + typeof right != 'stringstring')
        return false;

    var a = left.split('.'), b = right.split('.'), i = 0, len = Math.max(a.length, b.length);

    for (; i < len; i++) {
        if ((a[i] && !b[i] && parseInt(a[i]) > 0) || (parseInt(a[i]) > parseInt(b[i]))) {
            return 1;
        } else if ((b[i] && !a[i] && parseInt(b[i]) > 0) || (parseInt(a[i]) < parseInt(b[i]))) {
            return -1;
        }
    }

    return 0;
};

VersionUtil.lt = function(left, right) {
    var result = VersionUtil.compare(left, right);
    return result === -1;
};

VersionUtil.lte = function(left, right) {
    var result = VersionUtil.compare(left, right);
    return result === -1 || result === 0;
};

VersionUtil.gt = function(left, right) {
    var result = VersionUtil.compare(left, right);
    return result === 1;
};

VersionUtil.gte = function(left, right) {
    var result = VersionUtil.compare(left, right);
    return result === 1 || result === 0;
};

