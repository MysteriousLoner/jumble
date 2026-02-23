package asia.fourtitude.interviewq.jumble.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import asia.fourtitude.interviewq.jumble.TestConfig;
import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import asia.fourtitude.interviewq.jumble.model.GameGuessInput;
import asia.fourtitude.interviewq.jumble.model.GameGuessOutput;

@WebMvcTest(GameApiController.class)
@Import(TestConfig.class)
class GameApiControllerTest {

    static final ObjectMapper OM = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    JumbleEngine jumbleEngine;

    /*
     * NOTE: Refer to "RootControllerTest.java", "GameWebControllerTest.java"
     * as reference. Search internet for resource/tutorial/help in implementing
     * the unit tests.
     *
     * Refer to "http://localhost:8080/swagger-ui/index.html" for REST API
     * documentation and perform testing.
     *
     * Refer to Postman collection ("interviewq-jumble.postman_collection.json")
     * for REST API documentation and perform testing.
     */

    @Test
    void whenCreateNewGame_thenSuccess() throws Exception {
        /*
         * Doing HTTP GET "/api/game/new"
         *
         * Input: None
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Created new game."
         * c) `id` is not null
         * d) `originalWord` is not null
         * e) `scrambleWord` is not null
         * f) `totalWords` > 0
         * g) `remainingWords` > 0 and same as `totalWords`
         * h) `guessedWords` is empty list
         */
        MvcResult result = this.mvc.perform(get("/api/game/new")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);

        // a) HTTP status == 200  (asserted above via andExpect)
        // b) result equals "Created new game."
        assertEquals("Created new game.", output.getResult());
        // c) id is not null
        assertNotNull(output.getId());
        // d) originalWord is not null
        assertNotNull(output.getOriginalWord());
        // e) scrambleWord is not null
        assertNotNull(output.getScrambleWord());
        // f) totalWords > 0
        assertTrue(output.getTotalWords() > 0);
        // g) remainingWords > 0 and same as totalWords
        assertTrue(output.getRemainingWords() > 0);
        assertEquals(output.getTotalWords(), output.getRemainingWords());
        // h) guessedWords is empty list
        assertNotNull(output.getGuessedWords());
        assertTrue(output.getGuessedWords().isEmpty());
    }

    @Test
    void givenMissingId_whenPlayGame_thenInvalidId() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Input: JSON request body
         * a) `id` is null or missing
         * b) `word` is null/anything or missing
         *
         * Expect: Assert these
         * a) HTTP status == 404
         * b) `result` equals "Invalid Game ID."
         */
        GameGuessInput input = new GameGuessInput();
        // id is null (missing)
        input.setWord("anything");

        MvcResult result = this.mvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(OM.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);

        // b) result equals "Invalid Game ID."
        assertEquals("Invalid Game ID.", output.getResult());
    }

    @Test
    void givenMissingRecord_whenPlayGame_thenRecordNotFound() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Input: JSON request body
         * a) `id` is some valid ID (but not exists in game system)
         * b) `word` is null/anything or missing
         *
         * Expect: Assert these
         * a) HTTP status == 404
         * b) `result` equals "Game board/state not found."
         */
        GameGuessInput input = new GameGuessInput();
        input.setId(java.util.UUID.randomUUID().toString()); // valid UUID but not stored
        input.setWord("anything");

        MvcResult result = this.mvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(OM.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);

        // b) result equals "Game board/state not found."
        assertEquals("Game board/state not found.", output.getResult());
    }

    @Test
    void givenCreateNewGame_whenSubmitNullWord_thenGuessedIncorrectly() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is null or missing
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Guessed incorrectly."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` is equals to `input.word`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is equals to `remainingWords` of previous game state (no change)
         * i) `guessedWords` is empty list (because this is first attempt)
         */
        // First create a new game
        MvcResult newGameResult = this.mvc.perform(get("/api/game/new")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        GameGuessOutput newGame = OM.readValue(newGameResult.getResponse().getContentAsString(), GameGuessOutput.class);

        // Submit with null word
        GameGuessInput input = new GameGuessInput();
        input.setId(newGame.getId());
        // word is null

        MvcResult result = this.mvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(OM.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);

        // b) result equals "Guessed incorrectly."
        assertEquals("Guessed incorrectly.", output.getResult());
        // c) id equals to id of this game
        assertEquals(newGame.getId(), output.getId());
        // d) originalWord equals originalWord of this game
        assertEquals(newGame.getOriginalWord(), output.getOriginalWord());
        // e) scrambleWord is not null
        assertNotNull(output.getScrambleWord());
        // f) guessWord equals input.word (null)
        assertNull(output.getGuessWord());
        // g) totalWords equals totalWords of this game
        assertEquals(newGame.getTotalWords(), output.getTotalWords());
        // h) remainingWords equals remainingWords of previous game state (no change)
        assertEquals(newGame.getRemainingWords(), output.getRemainingWords());
        // i) guessedWords is empty list
        assertNotNull(output.getGuessedWords());
        assertTrue(output.getGuessedWords().isEmpty());
    }

    @Test
    void givenCreateNewGame_whenSubmitWrongWord_thenGuessedIncorrectly() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is some value (that is not correct answer)
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Guessed incorrectly."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` equals to input `guessWord`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is equals to `remainingWords` of previous game state (no change)
         * i) `guessedWords` is empty list (because this is first attempt)
         */
        // First create a new game
        MvcResult newGameResult = this.mvc.perform(get("/api/game/new")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        GameGuessOutput newGame = OM.readValue(newGameResult.getResponse().getContentAsString(), GameGuessOutput.class);

        // Submit with a wrong word (not a valid sub-word)
        String wrongWord = "zzz";
        GameGuessInput input = new GameGuessInput();
        input.setId(newGame.getId());
        input.setWord(wrongWord);

        MvcResult result = this.mvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(OM.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);

        // b) result equals "Guessed incorrectly."
        assertEquals("Guessed incorrectly.", output.getResult());
        // c) id equals to id of this game
        assertEquals(newGame.getId(), output.getId());
        // d) originalWord equals originalWord of this game
        assertEquals(newGame.getOriginalWord(), output.getOriginalWord());
        // e) scrambleWord is not null
        assertNotNull(output.getScrambleWord());
        // f) guessWord equals input guessWord
        assertEquals(wrongWord, output.getGuessWord());
        // g) totalWords equals totalWords of this game
        assertEquals(newGame.getTotalWords(), output.getTotalWords());
        // h) remainingWords equals remainingWords of previous game state (no change)
        assertEquals(newGame.getRemainingWords(), output.getRemainingWords());
        // i) guessedWords is empty list
        assertNotNull(output.getGuessedWords());
        assertTrue(output.getGuessedWords().isEmpty());
    }

    @Test
    void givenCreateNewGame_whenSubmitFirstCorrectWord_thenGuessedCorrectly() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is of correct answer
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Guessed correctly."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` equals to input `guessWord`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is equals to `remainingWords - 1` of previous game state (decrement by 1)
         * i) `guessedWords` is not empty list
         * j) `guessWords` contains input `guessWord`
         */
        // First create a new game
        MvcResult newGameResult = this.mvc.perform(get("/api/game/new")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        GameGuessOutput newGame = OM.readValue(newGameResult.getResponse().getContentAsString(), GameGuessOutput.class);

        // Get a correct sub-word from the engine
        java.util.Collection<String> subWords = jumbleEngine.generateSubWords(newGame.getOriginalWord(), 3);
        assertTrue(subWords.size() > 0, "There must be at least one sub word");
        String correctWord = subWords.iterator().next();

        GameGuessInput input = new GameGuessInput();
        input.setId(newGame.getId());
        input.setWord(correctWord);

        MvcResult result = this.mvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(OM.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);

        // b) result equals "Guessed correctly."
        assertEquals("Guessed correctly.", output.getResult());
        // c) id equals to id of this game
        assertEquals(newGame.getId(), output.getId());
        // d) originalWord equals originalWord of this game
        assertEquals(newGame.getOriginalWord(), output.getOriginalWord());
        // e) scrambleWord is not null
        assertNotNull(output.getScrambleWord());
        // f) guessWord equals input guessWord
        assertEquals(correctWord, output.getGuessWord());
        // g) totalWords equals totalWords of this game
        assertEquals(newGame.getTotalWords(), output.getTotalWords());
        // h) remainingWords == remainingWords - 1 of previous game state
        assertEquals(newGame.getRemainingWords() - 1, output.getRemainingWords());
        // i) guessedWords is not empty list
        assertNotNull(output.getGuessedWords());
        assertFalse(output.getGuessedWords().isEmpty());
        // j) guessedWords contains input guessWord
        assertTrue(output.getGuessedWords().contains(correctWord));
    }

    @Test
    void givenCreateNewGame_whenSubmitAllCorrectWord_thenAllGuessed() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         * b) has submit all correct answers, except the last answer
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is of the last correct answer
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "All words guessed."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` equals to input `guessWord`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is 0 (no more remaining, game ended)
         * i) `guessedWords` is not empty list
         * j) `guessWords` contains input `guessWord`
         */
        // First create a new game
        MvcResult newGameResult = this.mvc.perform(get("/api/game/new")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        GameGuessOutput newGame = OM.readValue(newGameResult.getResponse().getContentAsString(), GameGuessOutput.class);

        // Get all correct sub-words from the engine
        java.util.List<String> subWords = new java.util.ArrayList<>(
                jumbleEngine.generateSubWords(newGame.getOriginalWord(), 3));
        assertTrue(subWords.size() >= 2, "There must be at least 2 sub words to play");

        // Submit all words except the last
        for (int i = 0; i < subWords.size() - 1; i++) {
            GameGuessInput input = new GameGuessInput();
            input.setId(newGame.getId());
            input.setWord(subWords.get(i));
            this.mvc.perform(post("/api/game/guess")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(OM.writeValueAsString(input)))
                    .andExpect(status().isOk());
        }

        // Submit the last correct word
        String lastWord = subWords.get(subWords.size() - 1);
        GameGuessInput lastInput = new GameGuessInput();
        lastInput.setId(newGame.getId());
        lastInput.setWord(lastWord);

        MvcResult result = this.mvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(OM.writeValueAsString(lastInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);

        // b) result equals "All words guessed."
        assertEquals("All words guessed.", output.getResult());
        // c) id equals to id of this game
        assertEquals(newGame.getId(), output.getId());
        // d) originalWord equals originalWord of this game
        assertEquals(newGame.getOriginalWord(), output.getOriginalWord());
        // e) scrambleWord is not null
        assertNotNull(output.getScrambleWord());
        // f) guessWord equals input guessWord
        assertEquals(lastWord, output.getGuessWord());
        // g) totalWords equals totalWords of this game
        assertEquals(newGame.getTotalWords(), output.getTotalWords());
        // h) remainingWords is 0
        assertEquals(0, output.getRemainingWords());
        // i) guessedWords is not empty list
        assertNotNull(output.getGuessedWords());
        assertFalse(output.getGuessedWords().isEmpty());
        // j) guessedWords contains lastWord
        assertTrue(output.getGuessedWords().contains(lastWord));
    }

}
