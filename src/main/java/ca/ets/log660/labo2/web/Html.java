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

    static String normaliserUrlImage(String url) {
        if (url == null) {
            return null;
        }
        String u = url.trim().replace("ia.media-imdb.com", "m.media-amazon.com");
        if (u.startsWith("http://")) {
            u = "https://" + u.substring("http://".length());
        }
        return u.replaceAll("\\._V1.*", "._V1_.jpg");
    }

    static String page(String titre, String body) {
        return "<!doctype html><html lang=\"fr\"><head><meta charset=\"utf-8\">" +
                "<title>" + esc(titre) + "</title></head><body>" +
                "<p><a href=\"index.html\">Accueil</a> | <a href=\"films\">Films</a> | <a href=\"connexion\">Connexion</a></p>" +
                body + "</body></html>";
    }
}
