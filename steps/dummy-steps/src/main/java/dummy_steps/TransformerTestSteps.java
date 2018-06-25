package dummy_steps;

import com.testerum.api.annotations.steps.When;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class TransformerTestSteps {

    // todo: tests for injection within class hierarchy

    @When("I print String <<param>>")
    public void printString(final String param) {
        System.out.println("printString: [" + param + "]");
    }

    @When("I print boolean primitive <<param>>")
    public void printBooleanPrimitive(final boolean param) {
        System.out.println("printBoolean: [" + param + "]");
    }

    @When("I print Boolean object <<param>>")
    public void printBooleanObject(final Boolean param) {
        System.out.println("printBooleanObject: [" + param + "]");
    }

    @When("I print char primitive <<param>>")
    public void printCharPrimitive(final char param) {
        System.out.println("printCharPrimitive: [" + param + "]");
    }

    @When("I print Character object <<param>>")
    public void printCharacterObject(final Character param) {
        System.out.println("printCharacterObject: [" + param + "]");
    }

    @When("I print byte primitive <<param>>")
    public void printBytePrimitive(final byte param) {
        System.out.println("printBytePrimitive: [" + param + "]");
    }

    @When("I print Byte object <<param>>")
    public void printByteObject(final Byte param) {
        System.out.println("printByteObject: [" + param + "]");
    }

    @When("I print short primitive <<param>>")
    public void printShortPrimitive(final short param) {
        System.out.println("printShortPrimitive: [" + param + "]");
    }

    @When("I print Short object <<param>>")
    public void printShortObject(final Short param) {
        System.out.println("printShortObject: [" + param + "]");
    }

    @When("I print int primitive <<param>>")
    public void printIntPrimitive(final int param) {
        System.out.println("printIntPrimitive: [" + param + "]");
    }

    @When("I print Integer object <<param>>")
    public void printIntegerObject(final Integer param) {
        System.out.println("printIntegerObject: [" + param + "]");
    }

    @When("I print long primitive <<param>>")
    public void printLongPrimitive(final long param) {
        System.out.println("printLongPrimitive: [" + param + "]");
    }

    @When("I print Long object <<param>>")
    public void printLongObject(final Long param) {
        System.out.println("printLongObject: [" + param + "]");
    }

    @When("I print float primitive <<param>>")
    public void printFloatPrimitive(final float param) {
        System.out.println("printFloatPrimitive: [" + param + "]");
    }

    @When("I print Float object <<param>>")
    public void printFloatObject(final Float param) {
        System.out.println("printFloatObject: [" + param + "]");
    }

    @When("I print double primitive <<param>>")
    public void printDoublePrimitive(final double param) {
        System.out.println("printDoublePrimitive: [" + param + "]");
    }

    @When("I print Double object <<param>>")
    public void printDoubleObject(final Double param) {
        System.out.println("printDoubleObject: [" + param + "]");
    }

    @When("I print BigInteger <<param>>")
    public void printBigInteger(final BigInteger param) {
        System.out.println("printBigInteger: [" + param + "]");
    }

    @When("I print BigDecimal <<param>>")
    public void printBigDecimal(final BigDecimal param) {
        System.out.println("printBigDecimal: [" + param + "]");
    }

    @When("I print AtomicBoolean <<param>>")
    public void printAtomicBoolean(final AtomicBoolean param) {
        System.out.println("printAtomicBoolean: [" + param + "]");
    }

    @When("I print AtomicInteger <<param>>")
    public void printAtomicInteger(final AtomicInteger param) {
        System.out.println("printAtomicInteger: [" + param + "]");
    }

    @When("I print AtomicLong <<param>>")
    public void printAtomicLong(final AtomicLong param) {
        System.out.println("printAtomicLong: [" + param + "]");
    }

    @When("I print Color <<param>>")
    public void printColor(final Color param) {
        System.out.println("printColor: [" + param + "]");
    }

    public enum Color {
        RED,
        GREEN,
        BLUE,
        ;
    }
}
