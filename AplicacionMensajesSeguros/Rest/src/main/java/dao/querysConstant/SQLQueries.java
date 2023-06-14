package dao.querysConstant;

public class SQLQueries {

    public static final String INSERT_LOGIN_USER = "INSERT INTO login (nombre, contrasena,id_reader, codigo,date,verificado,mail,role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String DELETE_LOGIN_BY_ID_READER = "DELETE FROM login WHERE id_reader = ?";
    public static final String UPDATE_LOGIN_CONTRASENA = "UPDATE login SET contrasena = ? WHERE codigo = ?";
    public static final String UPDATE_LOGIN_REENVIO_MAIL = "update login SET date = ?, codigo = ? WHERE nombre = ? and mail = ?";
    public static final String ACTIVAR_LOGIN = "UPDATE login SET verificado = 1 WHERE codigo = ? and date <= ?";
    public static final String SELECT_LOGIN_USER_VERIFIED = "SELECT * FROM login WHERE nombre = ? and verificado = 1";
    public static final String SELECT_READERS_BY_NAME = "SELECT * FROM reader WHERE name_reader = ?";

    public static final String SELECT_LOGIN_USER = "SELECT * FROM login WHERE nombre = ? and mail = ?";
    public static final String SELECT_readers_QUERY = "select * from reader";
    public static final String DELETE_READ_ARTICLE_BY_IDREADER = "DELETE FROM readarticle WHERE id_reader = ?";
    public static final String DELETE_SUBSCRIPTIONS_BY_ID_READER = "DELETE FROM subscribe WHERE id_reader = ?";
    public static final String DELETE_READER = "delete from reader where id = ?";

    public static final String INSERT_NEWSPAPER = "insert into newspaper (nameNewspaper,releaseDate) values (?,?)";

    public static final String SELECT_READERS_BY_ID = "select * from reader where id = ?";

    public static final String SELECT_SUBSCRIPTIONS_QUERY_BY_ID_READER = "select * from subscribe where id_reader = ? and cancelation_date is null";
    public static final String INSERT_READER = "insert into reader (name_reader, birth_reader) values (?,?)";

    public static final String FILTER_READERS_BY_TYPE = "select * from reader where id in " +
            "(select id_reader from readarticle where id_article in " +
            "(select id from article where id_type = ?))";

    public static final String FILTER_READERS_BY_NEWSPAPER = "select * from reader where id in " +
            "(select id_reader from subscribe where id_newspaper in" +
            "(select id from newspaper where id = ?))";
    public static final String DELETE_READ_ARTICLE = "delete from readarticle where id_reader = ?";

    public static final String SELECT_count_readers_by_article = "select count(*) from reader where id in" +
            "(select id_reader from readarticle where id_article in" +
            "(select id from article where id = ?))";
    public static final String SELECT_5_READER_NW1_ORDERBY_SIGNING_DATE = "select * from reader where id in (select id_reader from subscribe where id_newspaper in (select id from newspaper where nameNewspaper = 'news_1') order by signing_date)";
    public static final String DELETE_SUBSCRIBE = "delete from subscribe where id_reader = ?";

    public static final String INSERT_READ_ARTICLE = "INSERT INTO readarticle (id_article, id_reader, ranking) VALUES (?, ?, ?)";

    public static final String SELECT_readarticles_QUERY = "select * from readarticle";

    public static final String DELETE_READ_ARTICLE_BY_IDNEWSPAPER = "delete from readarticle where id_article in (select id from article where id_newspaper = ?)";

    public static final String UPDATE_READER = "update reader set name_reader = ?, birth_reader = ? where id = ?";

    public static final String INSERT_SUBSCRIPTIONS_QUERY = "insert into subscribe (id_newspaper, id_reader, signing_date) values (?,?,?)";

    public static final String UPDATE_SUBSCRIPTIONS_QUERY = "update subscribe set id_newspaper = ?, signing_date = ?, cancelation_date = ? where id_reader = ? and id_newspaper = ?";

    public static final String SELECT_ALL_NEWSPAPERS = "select * from newspaper";

    public static final String SELECT_ALL_ARTICLES = "select * from article";

    public static final String DELETE_ARTICLE_BY_ID_NEWSPAPER = "delete from article where id_newspaper = ?";

    public static final String DELETE_NEWSPAPER_BY_ID = "delete from newspaper where id = ?";

    public static final String DELETE_SUBSCRIPTIONS_BY_ID_NEWSPAPER = "delete from subscribe where id_newspaper = ?";

    public static final String UPDATE_NEWSPAPER = "update newspaper set nameNewspaper = ?, releaseDate = ? where id = ?";


}
