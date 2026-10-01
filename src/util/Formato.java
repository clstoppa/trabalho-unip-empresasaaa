package util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Conversões entre texto digitado pelo usuário (padrão brasileiro) e valores do sistema. */
public final class Formato {

    private static final Locale BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Formato() {
    }

    public static String moeda(double v) {
        return NumberFormat.getCurrencyInstance(BR).format(v).replace('\u00A0', ' ');
    }

    /** Número para campo de edição: 1234,50 */
    public static String decimal(double v) {
        return new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(BR)).format(v);
    }

    /** Quantidades: até 3 casas, sem zeros sobrando. */
    public static String quantidade(double v) {
        return new DecimalFormat("#,##0.###", DecimalFormatSymbols.getInstance(BR)).format(v);
    }

    public static String data(LocalDate d) {
        return d == null ? "" : d.format(DATA);
    }

    public static double arredondar(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    public static String digitos(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }

    /** Lê "1.234,56" ou "1234.56". Campo vazio = 0. */
    public static double numero(String texto, String campo) throws NegocioException {
        if (texto == null || texto.trim().isEmpty()) {
            return 0;
        }
        String t = texto.trim().replace("R$", "").replace(" ", "");
        if (t.contains(",")) {
            t = t.replace(".", "").replace(',', '.');
        }
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException e) {
            throw new NegocioException("Valor inválido no campo \"" + campo + "\".");
        }
    }

    /** Lê dd/MM/aaaa. Campo vazio = null. */
    public static LocalDate data(String texto, String campo) throws NegocioException {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim(), DATA);
        } catch (DateTimeParseException e) {
            throw new NegocioException("Data inválida no campo \"" + campo + "\" (use dd/mm/aaaa).");
        }
    }
}
