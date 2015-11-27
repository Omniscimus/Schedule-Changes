package net.omniscimus.profielwerkstuk.text;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Verwerker voor het HTML bestand met roosterwijzigingen.
 *
 * @author omniscimus
 */
public class NehalenniaFileProcessor {

    /**
     * Verwerkt het gegeven bestand n.a.v. het formaat van het bestand met
     * roosterwijzigingen van het Nehalennia.
     *
     * @param sourceFilePath het pad naar het te verwerken bestand
     * @param toFilePath het pad naar het bestand waarin de resultaten moeten
     * komen
     * @return het nieuwe bestand met verwerkte roosterwijzigingen.
     * @throws IOException als een bestand niet geopend kon worden
     */
    public static File processFile(Path sourceFilePath, Path toFilePath) throws IOException {

	Stream<String> fileLinesStream = fileAsLinesStream(sourceFilePath, "windows-1252");
	ArrayList<ArrayList<String>> paragraphTagsInLines = getParagraphTagsInLines(fileLinesStream);
	ArrayList<String> paragraphTags = concatenateParagraphTagLines(paragraphTagsInLines);
	Stream<String> textLines = convertHTMLToText(paragraphTags);
	ArrayList<String> polishedTextLines = polishLegibleText(textLines);

	File toFile = toFilePath.toFile();
	try (PrintWriter writer = new PrintWriter(toFile, "UTF-8")) {
	    polishedTextLines.stream().forEach((line) -> {
		writer.println(line);
	    });
	}
	return toFile;

    }

    /**
     * Laadt alle regels van het bestand gedefinieerd door sourceFilePath in een
     * Stream, met als voorkeur de Charset "windows-1252", de Charset van het
     * bestand met roosterwijzigingen.
     *
     * @param sourceFilePath het pad naar het te laden bestand
     * @param charset de encoding die de voorkeur heeft
     * @return een Stream met alle regels van het opgegeven bestand
     * @throws IOException als het bestand niet geopend kon worden
     */
    public static Stream<String> fileAsLinesStream(Path sourceFilePath, String charset) throws IOException {
	Stream<String> lines;
	if (Charset.isSupported(charset)) {
	    lines = Files.lines(sourceFilePath, Charset.forName(charset));
	} else {
	    lines = Files.lines(sourceFilePath);
	}
	return lines;
    }

    /**
     * Filtert alle paragraph tags uit de Stream.
     *
     * @param fileLines de regels uit een HTML-bestand, op de goede volgorde
     * @return een ArrayList met als inhoud per tag een ArrayList met daarin de
     * regels die erbij horen
     */
    public static ArrayList<ArrayList<String>> getParagraphTagsInLines(Stream<String> fileLines) {
	ArrayList<ArrayList<String>> paragraphTagsInLines = new ArrayList<>();
	fileLines.forEach((line) -> {
	    if (line.startsWith("<p")) {
		ArrayList<String> paragraphTagInLines = new ArrayList<>();
		paragraphTagInLines.add(line);
		paragraphTagsInLines.add(paragraphTagInLines);
	    } else {
		if (!paragraphTagsInLines.isEmpty()) {
		    ArrayList<String> previousParagraphTag = paragraphTagsInLines.get(paragraphTagsInLines.size() - 1);
		    // Als de vorige tag al beëindigd is d.m.v. een </p>
		    // tag en deze line start niet met een nieuwe <p> tag, moet
		    // deze line genegeerd worden.
		    if (!previousParagraphTag.get(previousParagraphTag.size() - 1).endsWith("</p>")) {
			paragraphTagsInLines.get(paragraphTagsInLines.size() - 1).add(" " + line);
		    }
		}
	    }
	});
	return paragraphTagsInLines;
    }

    /**
     * Plakt alle Strings uit een ArrayList met Strings achter elkaar.
     *
     * @param arrayList een lijst met Strings die samengevoegd moeten worden
     * @return alle Strings uit arrayList samengevoegd
     */
    public static String concatenateStringArrayList(ArrayList<String> arrayList) {
	StringBuilder lineBuilder = new StringBuilder();
	arrayList.stream().forEach((str) -> {
	    lineBuilder.append(str);
	});
	return lineBuilder.toString();
    }

    /**
     * Vervangt een ArrayList met paragraph tags die onderverdeeld zijn in
     * verschillende regels, door een ArrayList met de tags in enkele Strings.
     * Zorgt ervoor dat tags die een linkermarge hebben, bij de tag ervoor zijn.
     *
     * @param paragraphTagLines een ArrayList met paragraph tags die
     * onderverdeeld zijn in meerdere regels
     * @return een ArrayList met de tags in enkele Strings
     */
    public static ArrayList<String> concatenateParagraphTagLines(ArrayList<ArrayList<String>> paragraphTagLines) {
	ArrayList<String> paragraphTags = new ArrayList<>();
	paragraphTagLines.stream().map((paragraphTagLine) -> concatenateStringArrayList(paragraphTagLine)).forEach((paragraphTag) -> {
	    paragraphTags.add(paragraphTag);
	});
	return paragraphTags;
    }

    /**
     * Filtert de leesbare tekst uit de HTML code.
     *
     * @param paragraphTags een lijst van HTML nodes
     * @return een Stream met de leesbare substrings van paragraphTags
     */
    public static Stream<String> convertHTMLToText(ArrayList<String> paragraphTags) {
	ArrayList<String> linesWithoutHTML = new ArrayList<>();
	// Regular Expression:
	// het mag geen < of > bevatten maar moet wel tussen > en < staan
	Pattern pattern = Pattern.compile(">[^<>]+<");
	return paragraphTags.stream().map((line) -> pattern.matcher(line)).map((matcher) -> {
	    StringBuilder lineWithoutHTMLBuilder = new StringBuilder();
	    while (matcher.find()) {
		if (!matcher.group().equals(">&nbsp;<")) {
		    lineWithoutHTMLBuilder.append(matcher.group().substring(1, matcher.group().length() - 1));
		}
	    }
	    return lineWithoutHTMLBuilder.toString();
	});
    }

    /**
     * Haalt HTML entities weg en verwijdert lege regels en overbodige regels
     * zoals "ROOSTERWIJZIGINGEN VOOR datum".
     *
     * @param strings een lijst met regels die verbeterd moet worden
     * @return de verbeterde lijst met regels
     */
    public static ArrayList<String> polishLegibleText(Stream<String> strings) {
	ArrayList<String> polishedLines = new ArrayList<>();
	strings.forEach((lineWithoutHTMLBuilder) -> {
	    String lineWithoutHTML = replaceHTMLEntities(lineWithoutHTMLBuilder);
	    if (!lineWithoutHTML.isEmpty() && !lineWithoutHTML.replaceAll("\\s", "").isEmpty()
		    && !lineWithoutHTML.startsWith("ROOSTERWIJZIGINGEN VOOR")) {
		polishedLines.add(lineWithoutHTML);
	    }
	});
	return polishedLines;
    }

    static HashMap<String, Character> htmlEntities;

    /**
     * Vervangt HTML entities zoals &nbsp; door de bijbehorende characters.
     *
     * @param input de String waarvan de HTML entities vervangen moeten worden
     * @return een String waarin de HTML entities vervangen zijn
     */
    public static String replaceHTMLEntities(String input) {
	if (htmlEntities == null) {
	    htmlEntities = new HashMap<>();
	    htmlEntities.put("&amp;", '&');// o&o
	    htmlEntities.put("&nbsp;", ' ');
	}
	for (String entity : htmlEntities.keySet()) {
	    input = input.replace(entity, htmlEntities.get(entity).toString());
	}
	return input;
    }

}
