import cucumber.annotation.en.Given;
import cucumber.annotation.en.When;
import cucumber.runtime.PendingException;
import cucumber.table.DataTable;

public class CustomerBank {
    @Given("^the following users$")
    public void the_following_users(final DataTable arg1) throws Throwable {
    }

    @Given("^the following system are available$")
    public void the_following_system_are_available(DataTable arg1) throws Throwable {
        // Express the Regexp above with the code you wish you had
        // For automatic conversion, change DataTable to List<YourType>
        throw new PendingException();
    }

    @Given("^the users connect to the systems$")
    public void the_users_connect_to_the_systems() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @When("^users submit messages as follows$")
    public void users_submit_messages_as_follows(DataTable arg1) throws Throwable {
        // Express the Regexp above with the code you wish you had
        // For automatic conversion, change DataTable to List<YourType>
        throw new PendingException();
    }
}
