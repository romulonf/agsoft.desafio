package controllers;

import constants.Constantes;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.math.RandomUtils;
import play.mvc.Controller;

import java.math.BigDecimal;

public class Application extends Controller {

    enum Operation {

        ADD("+"),
        SUBTRACT("-"),
        MULTIPLY("*");

        final String symbol;

        Operation(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

    }

    public static void index() {

        session.remove(Constantes.CAPTCHA_KEY);

        render();

    }

    public static void challenge(String requestToken, String captcha) {

        checkCaptcha(captcha);

        checkAuthenticity();

        session.remove(Constantes.AT_KEY);

        int pagina = 1;

        String responseToken = Constantes.TOKENS.get(0);

        if (StringUtils.isNotBlank(requestToken)) {

            int index = Constantes.TOKENS.indexOf(requestToken) + 1;

            if (index == Constantes.TOKENS.size()) {
                index = 0;
            }

            responseToken = Constantes.TOKENS.get(index);

            pagina = index + 1;

        }

        BigDecimal operatorA = randomNumber();

        BigDecimal operatorB = randomNumber();

        Operation operation = randomOperation();

        String symbol = operation.getSymbol();

        BigDecimal nextCaptcha = calculate(operatorA, operatorB, operation);

        session.put(Constantes.CAPTCHA_KEY, nextCaptcha);

        render(pagina, operatorA, operatorB, symbol, responseToken);

    }

    private static void checkCaptcha(String captcha) {

        if (!session.contains(Constantes.CAPTCHA_KEY)) {
            return;
        }

        try {

            BigDecimal expectedCaptcha = new BigDecimal(session.get(Constantes.CAPTCHA_KEY));

            if (StringUtils.isBlank(captcha) || !NumberUtils.isNumber(captcha)) {
                throw new RuntimeException();
            }

            BigDecimal requestCaptcha = new BigDecimal(captcha);

            if (!expectedCaptcha.equals(requestCaptcha)) {
                throw new RuntimeException();
            }

        } catch (Exception ex) {
            error("O captcha informado é inválido");
        }

    }

    private static BigDecimal calculate(BigDecimal operatorA, BigDecimal operatorB, Operation operation) {

        switch (operation) {
            case ADD:
                return operatorA.add(operatorB);
            case SUBTRACT:
                return operatorA.subtract(operatorB);
            case MULTIPLY:
                return operatorA.multiply(operatorB);
        }

        return null;

    }

    private static Operation randomOperation() {

        Operation[] operations = Operation.values();

        return operations[RandomUtils.nextInt(operations.length)];

    }

    private static BigDecimal randomNumber() {
        return new BigDecimal(RandomUtils.nextInt(Constantes.OPERATOR_SIZE));
    }

}