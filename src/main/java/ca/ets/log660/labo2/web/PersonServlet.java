package ca.ets.log660.labo2.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.ets.log660.labo2.facade.WebflixFacade;
import ca.ets.log660.labo2.model.Film;
import ca.ets.log660.labo2.model.Personne;

@WebServlet("/personne")
public class PersonServlet extends HttpServlet {
    private final WebflixFacade facade = new WebflixFacade();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        Integer personneId = parseId(request.getParameter("id"));
        Personne personne = personneId == null ? null : facade.consulterPersonne(personneId);
        if (personne == null) {
            response.getWriter().write(Html.page("Personne introuvable", "<h1>Personne introuvable</h1>"));
            return;
        }

        StringBuilder body = new StringBuilder();
        body.append("<h1>").append(Html.esc(personne.getNomComplet())).append("</h1>");

        if (personne.getPhoto() != null && personne.getPhoto().getUrl() != null) {
            String urlPhoto = Html.normaliserUrlImage(personne.getPhoto().getUrl());
            body.append("<p><img src=\"").append(Html.esc(urlPhoto))
                    .append("\" alt=\"Photo\" referrerpolicy=\"no-referrer\" style=\"max-width:200px\"></p>");
            body.append("<p><small>Photo: <a href=\"").append(Html.esc(urlPhoto)).append("\">")
                    .append(Html.esc(urlPhoto)).append("</a></small></p>");
        }

        body.append("<p><strong>Date de naissance:</strong> ").append(Html.esc(formaterDate(personne.getDateNaissance()))).append("</p>");
        body.append("<p><strong>Lieu de naissance:</strong> ").append(Html.esc(personne.getLieuNaissance())).append("</p>");
        body.append("<p><strong>Biographie:</strong> ").append(Html.esc(personne.getBio())).append("</p>");

        if (!personne.getFilmsCommeRealisateur().isEmpty()) {
            body.append("<h2>Films comme realisateur</h2><p>").append(liensFilms(personne.getFilmsCommeRealisateur())).append("</p>");
        }
        if (!personne.getFilmsCommeActeur().isEmpty()) {
            body.append("<h2>Films comme acteur</h2><p>").append(liensFilms(personne.getFilmsCommeActeur())).append("</p>");
        }

        response.getWriter().write(Html.page("Detail de la personne", body.toString()));
    }

    private String liensFilms(Collection<Film> films) {
        return films.stream()
                .sorted(Comparator.comparing(Film::getAnnee, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(f -> "<a href=\"film?id=" + f.getId() + "\">" + Html.esc(f.getTitre()) + " (" + Html.esc(f.getAnnee()) + ")</a>")
                .collect(Collectors.joining(", "));
    }

    private String formaterDate(Date date) {
        return date == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private Integer parseId(String id) {
        try {
            return id == null ? null : Integer.valueOf(id);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
