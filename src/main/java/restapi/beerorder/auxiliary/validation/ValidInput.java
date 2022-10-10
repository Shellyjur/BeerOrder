package restapi.beerorder.auxiliary.validation;

public class ValidInput {
    public static boolean checkIfValid(Object input){
        String regex = "[0-9]+";

        return (input.toString().matches(regex));
    }
}