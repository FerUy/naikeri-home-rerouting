import static org.junit.Assert.assertEquals;
import com.naikeri.sgw.impl.rules.MSRNNumbers;
import org.junit.Test;

public class TestMSRNGeneration {

  String dcdPN = "972544130100,972544130700";
  String drange = "100, 200";
  Long ddialogId = 0L;
  String dcallingParty = "987654321234";

  String cdPN = "972544130000";
  String range = "200";
  Long dialogId = 0L;
  String callingParty = "987654321234";

  @Test
  public void TestInitialRangeNumber() {
    // This test should return the first number in the range.
    String ruleName = "TestInitialRangeNumber";
    String result =
        MSRNNumbers.instance().getMSRNAddress(ruleName, cdPN, range, dialogId, callingParty);
    assertEquals(result, cdPN);
  }

  @Test
  public void TextNextNumberAfterInitialNumber() {
    // this test should return the next number in the range.
    String ruleName = "TextNextNumberAfterInitialNumber";
    // first cycle
    MSRNNumbers.instance().getMSRNAddress(ruleName, cdPN, range, dialogId, callingParty);
    // second cycle
    String result =
        MSRNNumbers.instance().getMSRNAddress(ruleName, cdPN, range, dialogId, callingParty);
    assertEquals("972544130001", result);
  }

  @Test
  public void TestTheWholeRangeOfNumbers() {
    // this will test the whole numbers in the range and return the last one.
    // note the cdpn numbers are inclusive
    String ruleName = "TestTheWholeRangeOfNumbers";
    Integer rangeNumber = Integer.parseInt(range);
    int counter = 0;
    do {
      MSRNNumbers.instance().getMSRNAddress(ruleName, cdPN, range, dialogId, callingParty);
      counter++;
    } while (counter < rangeNumber);
    String result =
        MSRNNumbers.instance().getMSRNAddress(ruleName, cdPN, range, dialogId, callingParty);
    assertEquals("972544130200", result);
  }

  @Test
  public void TestWholeRangeAndResetToInitial() {
    // this test should return the initial number in the range since all the numbers
    // are used up
    String ruleName = "TestWholeRangeAndResetToInitial";
    int counter = 0;
    Integer rNumber = Integer.parseInt(range);
    do {
      MSRNNumbers.instance().getMSRNAddress(ruleName, cdPN, range, dialogId, callingParty);
      counter++;
    } while (counter <= rNumber);
    String result =
        MSRNNumbers.instance().getMSRNAddress(ruleName, cdPN, range, dialogId, callingParty);
    assertEquals(result, cdPN);
  }

  @Test
  public void TestInitialRangeNumberWithMoreThanOneRange() {
    // This test is for cdpn with more than one range
    // cdpn = Range1, Range2
    // The test should return the first number in the cdpn range
    String ruleName = "TestInitialRangeNumberWithMoreThanOneRange";
    String result =
        MSRNNumbers.instance().getMSRNAddress(ruleName, dcdPN, drange, ddialogId, dcallingParty);
    assertEquals("972544130100", result);
  }

  @Test
  public void TestNextNumberWithMoreThanOneRange() {
    // this test should return the next number in range
    String ruleName = "TestNextNumberWithMoreThanOneRange";
    MSRNNumbers.instance().getMSRNAddress(ruleName, dcdPN, drange, ddialogId, dcallingParty);
    String result =
        MSRNNumbers.instance().getMSRNAddress(ruleName, dcdPN, drange, ddialogId, dcallingParty);
    assertEquals("972544130101", result);
  }

  @Test
  public void TestDoubleRangeNextRange() {
    // This test should return the initial number in the next range.
    // for exampe cdpn = Range1, Range2, the test should return the initial number in Range2
    String ruleName = "TestDoubleRangeNextRange";
    int counter = 0;
    do {
      MSRNNumbers.instance().getMSRNAddress(ruleName, dcdPN, drange, ddialogId, dcallingParty);
      counter++;
    } while (counter <= 100);
    String result =
        MSRNNumbers.instance().getMSRNAddress(ruleName, dcdPN, drange, ddialogId, dcallingParty);
    assertEquals("972544130700", result);
  }

  @Test
  public void TestToRecycleBackToTheInitialNumberRange(){
    // Test the two range and return the intial number after the cycle is used up
    // String drange = "100, 200";
    // String dcdPN = "972544130100,972544130700";
    String ruleName = "TestToRecycleBackToTheInitialNumberRange";
    int counter = 0;
    do {
      MSRNNumbers.instance().getMSRNAddress(ruleName, dcdPN, drange, ddialogId, dcallingParty);
      counter++;
    } while (counter <= 301);
    String result =
        MSRNNumbers.instance().getMSRNAddress(ruleName, dcdPN, drange, ddialogId, dcallingParty);
    assertEquals("972544130100", result);
  }
  @Test
  public void TestWithThreeRangeNumberRanges(){
    // testing with three number ranges
    String tcdPN = "972544130100,972544130700, 972554130300";
    String trange = "100,200,100";
    String ruleName = "TestWithThreeRangeNumberRanges";
    int counter = 0;
    do {
      MSRNNumbers.instance().getMSRNAddress(ruleName, tcdPN, trange, dialogId, callingParty);
      counter++;
    } while (counter <= 301);
    String result =
        MSRNNumbers.instance().getMSRNAddress(ruleName, tcdPN, trange, dialogId, callingParty);
    assertEquals("972554130300", result);
  }
  @Test 
  public void TestWithThreeRangeNumberRangesAndRecycleToInitial() {
    String tcdPN = "972544130100,972544130700, 972554130300";
    String trange = "100,200,100";
    String ruleName = "TestWithThreeRangeNumberRangesAndRecycleToInitial";
    int counter = 0;
    do {
      MSRNNumbers.instance().getMSRNAddress(ruleName, tcdPN, trange, dialogId, callingParty);
      counter++;
    } while (counter <= 402);
    String result =
        MSRNNumbers.instance().getMSRNAddress(ruleName, tcdPN, trange, dialogId, callingParty);
    assertEquals("972544130100", result);
  }
}
