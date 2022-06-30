package nl.han.ica.oose.dea.testedfizzbuzz;

public class FizzBuzzExecutor {

    public String execute(int i)  {
        if (i < 0)
            throw new NegativeInputException("Input should be a positive integer");

        var returnValue = new StringBuilder();

        if (i % 3 == 0) {
            returnValue.append("Fizz");
        }
        if (i % 5 == 0) {
            returnValue.append("Buzz");
        }
        if (returnValue.length() == 0) {
            returnValue.append(i);
        }

        return returnValue.toString();
    }
}
