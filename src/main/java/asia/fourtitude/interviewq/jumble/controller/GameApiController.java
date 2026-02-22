package asia.fourtitude.interviewq.jumble.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asia.fourtitude.interviewq.jumble.core.GameState;
import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import asia.fourtitude.interviewq.jumble.model.GameGuessInput;
import asia.fourtitude.interviewq.jumble.model.GameGuessModel;
import asia.fourtitude.interviewq.jumble.model.GameGuessOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Game API", description = "Guessing words game REST API endpoint.")
@RequestMapping(path = "/api/game")
public class GameApiController {

    private static final Logger LOG = LoggerFactory.getLogger(GameApiController.class);

    private final JumbleEngine jumbleEngine;

    /*
     * In-memory database/repository for all the game boards/states.
     */
    private final Map<String, GameGuessModel> gameBoards;

    @Autowired(required = true)
    public GameApiController(JumbleEngine jumbleEngine) {
        this.jumbleEngine = jumbleEngine;
        this.gameBoards = new ConcurrentHashMap<>();
    }

    @Operation(
            summary = "Creates new game board/state",
            description = "Creates a new game board/state and registered into game engine referenced by `id`. All subsequent operation/play is tied to `id`.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = GameGuessOutput.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    description = "Created a new game/board and registered into system.",
                                                    value = "{\n" +
                                                            "  \"result\": \"Created new game.\",\n" +
                                                            "  \"id\": \"65e0d7a4-59bf-4065-beb1-3c2220d87e1e\",\n" +
                                                            "  \"original_word\": \"titans\",\n" +
                                                            "  \"scramble_word\": \"nisatt\",\n" +
                                                            "  \"total_words\": 29,\n" +
                                                            "  \"remaining_words\": 29,\n" +
                                                            "  \"guessed_words\": []\n" +
                                                            "}") })) })
    @GetMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameGuessOutput> newGame() {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */
        GameState gameState = this.jumbleEngine.createGameState(6, 3);

        /*
         * a) Store the game state to the repository, with unique game board ID
         * b) Return the game board/state (GameGuessOutput) to caller
         */
        String id = java.util.UUID.randomUUID().toString();

        GameGuessModel model = new GameGuessModel();
        model.setId(id);
        model.setCreatedAt(new java.util.Date());
        model.setModifiedAt(new java.util.Date());
        model.setGameState(gameState);

        this.gameBoards.put(id, model);

        int totalWords = gameState.getSubWords().size();

        GameGuessOutput output = new GameGuessOutput();
        output.setResult("Created new game.");
        output.setId(id);
        output.setOriginalWord(gameState.getOriginal());
        output.setScrambleWord(gameState.getScramble());
        output.setTotalWords(totalWords);
        output.setRemainingWords(totalWords);
        output.setGuessedWords(new java.util.ArrayList<>());

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @Operation(
            summary = "Submits word to play the game",
            description = "Submits a guessed `word`, along with `id` to play the game.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = GameGuessOutput.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Guessed Correctly First Time",
                                                    description = "Guessed correctly the first time.",
                                                    value = "{\n" +
                                                            "  \"result\": \"Guessed correctly.\",\n" +
                                                            "  \"id\": \"88b4278c-5141-42af-86e6-2a1d4cfa5f3b\",\n" +
                                                            "  \"original_word\": \"ranker\",\n" +
                                                            "  \"scramble_word\": \"nekarr\",\n" +
                                                            "  \"guess_word\": \"rank\",\n" +
                                                            "  \"total_words\": 15,\n" +
                                                            "  \"remaining_words\": 14,\n" +
                                                            "  \"guessed_words\": [\n" +
                                                            "    \"rank\"\n" +
                                                            "  ]\n" +
                                                            "}"),
                                            @ExampleObject(
                                                    name = "Guessed Correctly Subsequent",
                                                    description = "Guessed correctly with subsequent word.",
                                                    value = "{\n" +
                                                            "  \"result\": \"Guessed correctly.\",\n" +
                                                            "  \"id\": \"e23a268c-e4af-4123-a610-755e34ac201c\",\n" +
                                                            "  \"original_word\": \"burger\",\n" +
                                                            "  \"scramble_word\": \"rerugb\",\n" +
                                                            "  \"guess_word\": \"rug\",\n" +
                                                            "  \"total_words\": 15,\n" +
                                                            "  \"remaining_words\": 7,\n" +
                                                            "  \"guessed_words\": [\n" +
                                                            "    \"bug\",\n" +
                                                            "    \"bur\",\n" +
                                                            "    \"err\",\n" +
                                                            "    \"rug\",\n" +
                                                            "    \"burr\",\n" +
                                                            "    \"grub\",\n" +
                                                            "    \"rube\",\n" +
                                                            "    \"urge\"\n" +
                                                            "  ]\n" +
                                                            "}"),
                                            @ExampleObject(
                                                    name = "Guessed Incorrectly",
                                                    description = "Guessed with incorrect word.",
                                                    value = "{\n" +
                                                            "  \"result\": \"Guessed incorrectly.\",\n" +
                                                            "  \"id\": \"88b4278c-5141-42af-86e6-2a1d4cfa5f3b\",\n" +
                                                            "  \"original_word\": \"ranker\",\n" +
                                                            "  \"scramble_word\": \"rnraek\",\n" +
                                                            "  \"guess_word\": \"answer\",\n" +
                                                            "  \"total_words\": 15,\n" +
                                                            "  \"remaining_words\": 15,\n" +
                                                            "  \"guessed_words\": []\n" +
                                                            "}"),
                                            @ExampleObject(
                                                    name = "All Guessed",
                                                    description = "All words guessed.",
                                                    value = "{\n" +
                                                            "  \"result\": \"All words guessed.\",\n" +
                                                            "  \"id\": \"353ee769-a472-4704-a5f2-d525f181a03e\",\n" +
                                                            "  \"original_word\": \"gloomy\",\n" +
                                                            "  \"scramble_word\": \"gomlyo\",\n" +
                                                            "  \"guess_word\": \"moo\",\n" +
                                                            "  \"total_words\": 9,\n" +
                                                            "  \"remaining_words\": 0,\n" +
                                                            "  \"guessed_words\": [\n" +
                                                            "    \"goo\",\n" +
                                                            "    \"gym\",\n" +
                                                            "    \"log\",\n" +
                                                            "    \"loo\",\n" +
                                                            "    \"moo\",\n" +
                                                            "    \"glom\",\n" +
                                                            "    \"logo\",\n" +
                                                            "    \"loom\",\n" +
                                                            "    \"gloom\"\n" +
                                                            "  ]\n" +
                                                            "}") })),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = GameGuessOutput.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Invalid ID",
                                                    description = "The input `ID` is invalid.",
                                                    value = "{\n" +
                                                            "  \"result\": \"Invalid Game ID.\"\n" +
                                                            "}"),
                                            @ExampleObject(
                                                    name = "Record not found",
                                                    description = "The `ID` is correct format, but game board/state is not found in system.",
                                                    value = "{\n" +
                                                            "  \"result\": \"Game board/state not found.\"\n" +
                                                            "}") })) })
    @PostMapping(value = "/guess", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameGuessOutput> playGame(
            @Parameter(
                    description = "Submits the `word` to guess.",
                    required = true,
                    schema = @Schema(implementation = GameGuessInput.class),
                    example = "{\n" +
                            "  \"id\": \"4579256c-326f-4169-9b56-6d1d1a2c11f0\",\n" +
                            "  \"word\": \"answer\"\n" +
                            "}")
            @RequestBody GameGuessInput input) {
        /*
         * Refer to the method's Javadoc (above) and implement accordingly.
         * Must pass the corresponding unit tests.
         */

        /*
         * a) Validate the input (GameGuessInput)
         */
        // Validate id: must be non-null and a valid UUID format
        String inputId = (input == null) ? null : input.getId();
        if (inputId == null || inputId.trim().isEmpty()) {
            GameGuessOutput invalid = new GameGuessOutput();
            invalid.setResult("Invalid Game ID.");
            return new ResponseEntity<>(invalid, HttpStatus.NOT_FOUND);
        }
        // Validate UUID format
        try {
            java.util.UUID.fromString(inputId.trim());
        } catch (IllegalArgumentException e) {
            GameGuessOutput invalid = new GameGuessOutput();
            invalid.setResult("Invalid Game ID.");
            return new ResponseEntity<>(invalid, HttpStatus.NOT_FOUND);
        }

        /*
         * b) Check records exists in repository (search by input `id`)
         */
        GameGuessModel model = this.gameBoards.get(inputId.trim());
        if (model == null) {
            GameGuessOutput notFound = new GameGuessOutput();
            notFound.setResult("Game board/state not found.");
            return new ResponseEntity<>(notFound, HttpStatus.NOT_FOUND);
        }

        /*
         * c) From the input guessing `word`, implement the game logic
         * d) Update the game board (and game state) in repository
         */
        GameState gameState = model.getGameState();
        String guessWord = (input.getWord() == null) ? null : input.getWord().trim();

        // Rescramble on every play
        gameState.setScramble(this.jumbleEngine.scramble(gameState.getOriginal()));
        model.setModifiedAt(new java.util.Date());

        int totalWords = gameState.getSubWords().size();
        long guessedCount = gameState.getSubWords().values().stream()
                .filter(Boolean.TRUE::equals).count();
        int remainingWords = totalWords - (int) guessedCount;

        String result;
        boolean guessedCorrectly = false;

        if (guessWord == null || guessWord.isEmpty()) {
            // null / blank word → incorrect
            result = "Guessed incorrectly.";
        } else {
            Boolean existing = gameState.getSubWords().get(guessWord);
            if (existing == null) {
                // word not in sub-words list
                result = "Guessed incorrectly.";
            } else if (existing == Boolean.TRUE) {
                // already guessed before
                result = "Guessed incorrectly.";
            } else {
                // correct new guess
                gameState.updateGuessWord(guessWord);
                guessedCorrectly = true;
                guessedCount++;
                remainingWords = totalWords - (int) guessedCount;
                if (remainingWords == 0) {
                    result = "All words guessed.";
                } else {
                    result = "Guessed correctly.";
                }
            }
        }

        /*
         * e) Return the updated game board/state (GameGuessOutput) to caller
         */
        GameGuessOutput output = new GameGuessOutput();
        output.setResult(result);
        output.setId(model.getId());
        output.setOriginalWord(gameState.getOriginal());
        output.setScrambleWord(gameState.getScramble());
        output.setGuessWord(guessWord);
        output.setTotalWords(totalWords);
        output.setRemainingWords(remainingWords);
        output.setGuessedWords(gameState.getGuessedWords());

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

}
