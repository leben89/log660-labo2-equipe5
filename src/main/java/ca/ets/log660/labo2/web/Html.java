package ca.ets.log660.labo2.web;

final class Html {
    private Html() {
    }

    static String esc(Object valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    static String page(String titre, String body) {
        return "<!doctype html><html lang=\"fr\"><head><meta charset=\"utf-8\">" +
                "<title>" + esc(titre) + "</title></head><body>" +
                "<p><a href=\"index.html\">Accueil</a> | <a href=\"films\">Films</a> | <a href=\"connexion\">Connexion</a></p>" +
                body + "</body></html>";
    }
}
