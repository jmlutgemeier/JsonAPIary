package test.com.cradlepoint.jsonapiary;

import com.cradlepoint.jsonapiary.JsonApiModule;
import com.cradlepoint.jsonapiary.types.JsonApiError;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Assert;
import org.junit.Test;
import test.com.cradlepoint.jsonapiary.pojos.*;

import java.net.URL;

public class JsonApiErrorTests {

    ////////////////
    // Attributes //
    ////////////////

    private ObjectMapper objectMapper;

    /////////////////
    // Constructor //
    /////////////////

    public JsonApiErrorTests() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JsonApiModule());
    }

    ///////////
    // Tests //
    ///////////

    @Test
    public void singleErrorTest() throws Exception {
        // Init Test Objects //
        JsonApiError jsonApiError = new JsonApiError()
                .withTitle("Some Title")
                .withDetail("some error detail!!! yay!")
                .withId("0xdeadbeef")
                .withLink("google", new URL("https://www.google.com"))
                .withLink("cradlepoint", new URL("https://cradlepoint.com/"))
                .withStatus(200)
                .withSourcePointer("/some/api/URI")
                .withMeta("aMeta", "thing");

        // Serialize and Verfiy //
        String json = objectMapper.writeValueAsString(jsonApiError);

        Assert.assertTrue(json.equals("{\n" +
                "  \"errors\" : [ {\n" +
                "    \"id\" : \"0xdeadbeef\",\n" +
                "    \"links\" : {\n" +
                "      \"google\" : \"https://www.google.com\",\n" +
                "      \"cradlepoint\" : \"https://cradlepoint.com/\"\n" +
                "    },\n" +
                "    \"status\" : \"200\",\n" +
                "    \"title\" : \"Some Title\",\n" +
                "    \"detail\" : \"some error detail!!! yay!\",\n" +
                "    \"source\" : {\n" +
                "      \"pointer\" : \"/some/api/URI\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"aMeta\" : \"thing\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}"));
    }

    @Test
    public void multipleErrorTest() throws Exception {
        // Init Test Objects //
        ABaseClass aBaseClass = new ABaseClass();
        aBaseClass.setWhoAmI("a ABaseClass");
        aBaseClass.setWhatDoIHave(null);

        JsonApiError jsonApiError =
                new JsonApiError()
                .withTitle("Some error title...")
                .withId("12345")
                .withMeta("someType", aBaseClass)
                .andError()
                .withSourcePointer("/api/v1/bad")
                .withCode("foobar")
                .andError()
                .withTitle("yet ANOTHER error")
                .withStatus(500)
                .withDetail("some really great detail!")
                .withMeta("anException", new IllegalArgumentException("an exception!").toString());

        // Serialize and Verify //
        String json =  objectMapper.writeValueAsString(jsonApiError);

        Assert.assertTrue(json.equals("{\n" +
                "  \"errors\" : [ {\n" +
                "    \"status\" : \"500\",\n" +
                "    \"title\" : \"yet ANOTHER error\",\n" +
                "    \"detail\" : \"some really great detail!\",\n" +
                "    \"meta\" : {\n" +
                "      \"anException\" : \"java.lang.IllegalArgumentException: an exception!\"\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"code\" : \"foobar\",\n" +
                "    \"source\" : {\n" +
                "      \"pointer\" : \"/api/v1/bad\"\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : \"12345\",\n" +
                "    \"title\" : \"Some error title...\",\n" +
                "    \"meta\" : {\n" +
                "      \"someType\" : {\n" +
                "        \"whatDoIHave\" : null,\n" +
                "        \"whoAmI\" : \"a ABaseClass\",\n" +
                "        \"baseMaddness\" : \"what is a whaz?\"\n" +
                "      }\n" +
                "    }\n" +
                "  } ]\n" +
                "}"));
    }

    @Test
    public void duplicateErrorsTest() throws Exception {
        // Init Test Objects //
        JsonApiError jsonApiError = new JsonApiError()
                .withStatus(402)
                .withMeta("foo", "bar")
                .andError()
                .withStatus(402)
                .withMeta("foo", "bar");

        // Serialize and Verify //
        String json =  objectMapper.writeValueAsString(jsonApiError);

        Assert.assertTrue(json.equals("{\n" +
                "  \"errors\" : [ {\n" +
                "    \"status\" : \"402\",\n" +
                "    \"meta\" : {\n" +
                "      \"foo\" : \"bar\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}"));
    }

    @Test
    public void manyEmptyErrorsTest() throws Exception {
        // Init Test Objects //
        JsonApiError jsonApiError = new JsonApiError()
                .andError()
                .andError()
                .andError()
                .andError()
                .andError()
                .andError()
                .andError()
                .andError();

        // Serialize and Verify //
        String json =  objectMapper.writeValueAsString(jsonApiError);

        Assert.assertTrue(json.equals("{\n" +
                "  \"errors\" : [ ]\n" +
                "}"));
    }

    @Test
    public void loopConstructonTest() throws Exception {
        // Init Test Objects //
        JsonApiError jsonApiError = new JsonApiError();

        for(int i1 = 0; i1 < 5; i1++) {
            jsonApiError.andError()
                    .withTitle("loop #" + i1 + " error")
                    .withCode(String.valueOf(i1))
                    .withLink("cradlepoint", new URL("https://cradlepoint.com/"));
        }

        // Serialize and Verify //
        String json =  objectMapper.writeValueAsString(jsonApiError);

        Assert.assertTrue(json.equals("{\n" +
                "  \"errors\" : [ {\n" +
                "    \"links\" : {\n" +
                "      \"cradlepoint\" : \"https://cradlepoint.com/\"\n" +
                "    },\n" +
                "    \"code\" : \"0\",\n" +
                "    \"title\" : \"loop #0 error\"\n" +
                "  }, {\n" +
                "    \"links\" : {\n" +
                "      \"cradlepoint\" : \"https://cradlepoint.com/\"\n" +
                "    },\n" +
                "    \"code\" : \"3\",\n" +
                "    \"title\" : \"loop #3 error\"\n" +
                "  }, {\n" +
                "    \"links\" : {\n" +
                "      \"cradlepoint\" : \"https://cradlepoint.com/\"\n" +
                "    },\n" +
                "    \"code\" : \"1\",\n" +
                "    \"title\" : \"loop #1 error\"\n" +
                "  }, {\n" +
                "    \"links\" : {\n" +
                "      \"cradlepoint\" : \"https://cradlepoint.com/\"\n" +
                "    },\n" +
                "    \"code\" : \"4\",\n" +
                "    \"title\" : \"loop #4 error\"\n" +
                "  }, {\n" +
                "    \"links\" : {\n" +
                "      \"cradlepoint\" : \"https://cradlepoint.com/\"\n" +
                "    },\n" +
                "    \"code\" : \"2\",\n" +
                "    \"title\" : \"loop #2 error\"\n" +
                "  } ]\n" +
                "}"));
    }

}
