package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Utilitário JDBC usado pelos DAOs (PreparedStatement, conversão de tipos e mapeamento). */
public final class Sql {

    /** Converte uma linha do ResultSet em objeto. */
    public interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    private Sql() {
    }

    private static void parametros(PreparedStatement ps, Object... p) throws SQLException {
        for (int i = 0; i < p.length; i++) {
            Object v = p[i];
            if (v instanceof LocalDate) {
                v = v.toString();
            } else if (v instanceof Boolean) {
                v = ((Boolean) v) ? "S" : "N";
            }
            ps.setObject(i + 1, v);
        }
    }

    /** INSERT que devolve o código gerado. */
    public static int inserir(Connection c, String sql, Object... p) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            parametros(ps, p);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** UPDATE / DELETE. */
    public static int executar(Connection c, String sql, Object... p) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            parametros(ps, p);
            return ps.executeUpdate();
        }
    }

    public static <T> List<T> consultar(Connection c, String sql, Mapeador<T> m, Object... p) throws SQLException {
        List<T> lista = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            parametros(ps, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(m.mapear(rs));
                }
            }
        }
        return lista;
    }

    public static <T> T primeiro(Connection c, String sql, Mapeador<T> m, Object... p) throws SQLException {
        List<T> lista = consultar(c, sql, m, p);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public static LocalDate data(ResultSet rs, String coluna) throws SQLException {
        String s = rs.getString(coluna);
        return (s == null || s.length() < 10) ? null : LocalDate.parse(s.substring(0, 10));
    }

    public static boolean sim(ResultSet rs, String coluna) throws SQLException {
        return "S".equals(rs.getString(coluna));
    }

    public static String like(String filtro) {
        return "%" + filtro + "%";
    }
}
