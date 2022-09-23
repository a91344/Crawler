import java.math.BigInteger
import java.security.MessageDigest

object MD5Utils {
    public fun EncodeByMD5(str: String): String {
        try {
            // 生成一个MD5加密计算摘要
            val md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.toByteArray(Charsets.UTF_8));
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            val md5 = BigInteger(1, md.digest()).toString(16);
            //BigInteger会把0省略掉，需补全至32位
            return fillMD5(md5);
        } catch (e: Exception) {
            throw  RuntimeException("MD5加密错误:" + e.message, e);
        }
    }

    private fun fillMD5(md5: String): String {
        //如果不够32位则回调自身补零，最后返回32位长度的签名
        return if(md5.length == 32) md5 else fillMD5("0$md5");
    }
}