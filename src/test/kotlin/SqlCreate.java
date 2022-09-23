public class SqlCreate {
    public static final String CARTOON_SQL = "CREATE TABLE IF NOT EXISTS \"cartoon\" (\n" +
            "      \"_md5\" text(64) NOT NULL,\n" +
            "      \"title\" text(255),\n" +
            "      \"cover\" text(300),\n" +
            "      \"author\" text(255),\n" +
            "      \"tags\" text(255),\n" +
            "      \"pages\" integer,\n" +
            "      \"time_of_receipt\" integer,\n" +
            "      PRIMARY KEY (\"_md5\")\n" +
            "    );";
    public static final String CARTOON_IMGS_SQL = "CREATE TABLE IF NOT EXISTS \"cartoon_imgs\" (\n" +
            "                                    \"_md5\" text(64) NOT NULL,\n" +
            "                                    \"title\" text(255),\n" +
            "                                    \"imgs\" text,\n" +
            "                                    PRIMARY KEY (\"_md5\")\n" +
            "    );";
}
