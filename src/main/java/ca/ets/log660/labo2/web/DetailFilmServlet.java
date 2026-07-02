package ca.ets.log660.labo2.web;

import ca.ets.log660.labo2.facade.WebflixFacade;
import ca.ets.log660.labo2.model.Film;
import ca.ets.log660.labo2.model.FilmProduit;
import ca.ets.log660.labo2.model.Genre;
import ca.ets.log660.labo2.model.Pays;
import ca.ets.log660.labo2.model.Personne;
import ca.ets.log660.labo2.model.Photo;
import ca.ets.log660.labo2.model.Video;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/film")
public class DetailFilmServlet extends HttpServlet {
    private final WebflixFacade facade = new WebflixFacade();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        Integer filmId = parseId(request.getParameter("id"));
        Film film = filmId == null ? null : facade.consulterFilm(filmId);
        if (film == null) {
            response.getWriter().write(Html.page("Film introuvable", "<h1>Film introuvable</h1>"));
            return;
        }

        StringBuilder body = new StringBuilder();
        body.append("<h1>").append(Html.esc(film.getTitre())).append(" (").append(Html.esc(film.getAnnee())).append(")</h1>");

        for (Photo poster : film.getPosters()) {
            if (poster.getUrl() != null) {
                String urlPoster = Html.normaliserUrlImage(poster.getUrl());
                body.append("<p><img src=\"").append(Html.esc(urlPoster))
                        .append("\" alt=\"Affiche\" referrerpolicy=\"no-referrer\" style=\"max-width:200px\"></p>");
            }
        }
        body.append("<p><strong>Langue:</strong> ").append(Html.esc(film.getLangue())).append("</p>");
        body.append("<p><strong>Duree:</strong> ").append(Html.esc(film.getDuree())).append(" minutes</p>");
        body.append("<p><strong>Genres:</strong> ").append(Html.esc(film.getGenres().stream().map(Genre::getNomGenre).collect(Collectors.joining(", ")))).append("</p>");
        body.append("<p><strong>Pays:</strong> ").append(Html.esc(film.getPaysProduction().stream().map(Pays::getNomPays).collect(Collectors.joining(", ")))).append("</p>");
        body.append("<p><strong>Realisateurs:</strong> ").append(liensPersonnes(film.getRealisateurs())).append("</p>");
        body.append("<p><strong>Acteurs:</strong> ").append(liensPersonnes(film.getActeurs())).append("</p>");
        body.append("<p><strong>Resume:</strong> ").append(Html.esc(film.getResume())).append("</p>");

        if (!film.getBandesAnnonces().isEmpty()) {
            body.append("<h2>Bandes-annonces</h2><ul>");
            for (Video video : film.getBandesAnnonces()) {
                body.append("<li><a href=\"").append(Html.esc(video.getUrl())).append("\">").append(Html.esc(video.getUrl())).append("</a></li>");
            }
            body.append("</ul>");
        }

        body.append("<h2>Location</h2>");
        body.append("<table border=\"1\" cellpadding=\"5\"><tr><th>Produit</th><th>Copies disponibles</th><th>Statut</th><th>Action</th></tr>");
        for (FilmProduit produit : film.getProduits()) {
            body.append("<tr>")
                    .append("<td>").append(Html.esc(produit.getId())).append("</td>")
                    .append("<td>").append(Html.esc(produit.getCopieDispo())).append("</td>")
                    .append("<td>").append(Html.esc(produit.getStatut())).append("</td>")
                    .append("<td>");
            if (produit.getCopieDispo() != null && produit.getCopieDispo() > 0) {
                body.append("<form method=\"post\" action=\"louer\">")
                        .append("<input type=\"hidden\" name=\"filmProduitId\" value=\"").append(produit.getId()).append("\">")
                        .append("<button type=\"submit\">Louer</button>")
                        .append("</form>");
            } else {
                body.append("Indisponible");
            }
            body.append("</td></tr>");
        }
        body.append("</table>");

        response.getWriter().write(Html.page("Detail du film", body.toString()));
    }

    private String liensPersonnes(java.util.Collection<Personne> personnes) {
        return personnes.stream()
                .map(p -> "<a href=\"personne?id=" + p.getId() + "\">" + Html.esc(p.getNomComplet()) + "</a>")
                .collect(Collectors.joining(", "));
    }

    private Integer parseId(String id) {
        try {
            return id == null ? null : Integer.valueOf(id);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
