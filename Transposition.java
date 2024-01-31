import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class Transposition {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input file path
        System.out.print("Enter the path to the input JSON file: ");
        String inputFilePath = scanner.nextLine();

        // Number of semitones
        System.out.print("Enter the number of semitones to transpose: ");
        int semitones = scanner.nextInt();

        try {
            // Read the input JSON file
            String jsonString = new String(Files.readAllBytes(Paths.get(inputFilePath)));
            JSONArray musicalPiece = new JSONArray(jsonString);

            // Transpose the musical piece
            JSONArray transposedPiece = transposeMusicalPiece(musicalPiece, semitones);

            // Check if any note is out of range
            if (!isWithinKeyboardRange(transposedPiece)) {
                System.out.println("Error: Transposed notes fall out of the keyboard range.");
            } else {
                // Write the transposed musical piece to an output JSON file
                String outputFilePath = "transposed_output.json";
                Files.write(Paths.get(outputFilePath), transposedPiece.toString(2).getBytes());

                System.out.println("Transposition successful. Result saved to " + outputFilePath);
            }

        } catch (IOException | JSONException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static JSONArray transposeMusicalPiece(JSONArray musicalPiece, int semitones) {
        // Transpose a collection of notes
        JSONArray transposedPiece = new JSONArray();
        for (int i = 0; i < musicalPiece.length(); i++) {
            try {
                JSONArray note = musicalPiece.getJSONArray(i);
                transposedPiece.put(transposeNote(note, semitones));
            } catch (JSONException e) {
                throw new RuntimeException("Error transposing note: " + e.getMessage(), e);
            }
        }
        return transposedPiece;
    }

    private static JSONArray transposeNote(JSONArray note, int semitones) throws JSONException {
        // Transpose a single note
        int octave = note.getInt(0);
        int noteNumber = note.getInt(1);

        int newNoteNumber = (noteNumber + semitones - 1 + 12) % 12 + 1;
        int newOctave = octave + (noteNumber + semitones - newNoteNumber - 1) / 12;

        return new JSONArray(Arrays.asList(newOctave, newNoteNumber));
    }

    private static boolean isWithinKeyboardRange(JSONArray notes) throws JSONException {
        // Check if all transposed notes are within the keyboard range
        for (int i = 0; i < notes.length(); i++) {
            JSONArray note = notes.getJSONArray(i);
            int octave = note.getInt(0);
            int noteNumber = note.getInt(1);

            if (!(octave >= -3 && octave <= 5 && noteNumber >= 1 && noteNumber <= 12)) {
                return false;
            }
        }
        return true;
    }
}


