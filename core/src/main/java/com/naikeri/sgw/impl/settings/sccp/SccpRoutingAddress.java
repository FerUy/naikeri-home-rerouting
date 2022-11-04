package com.naikeri.sgw.impl.settings.sccp;

import java.util.ArrayList;
import java.util.List;

import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDOddEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.DefaultEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

public class SccpRoutingAddress {

  private String name;
  private int id;
  private RoutingIndicator routingIndicator;
  private String digits;
  private List<SccpAddress> sccpAddresses = new ArrayList<>();

  @Override
  public String toString() {
    return String.format("[<%s>]: Routing Indicator = %s, Digits = %s", name, routingIndicator.toString(), digits);
  }

  /**
   * Add a routing address
   * 
   * @param name             The name of the routing address
   * @param id               The id of the routing address
   * @param routingIndicator the routing indicator
   * @param digits           the digits
   */
  public SccpRoutingAddress(String name, int id, String routingIndicator, String digits) {
    this.id = id;
    RoutingIndicator ri = RoutingIndicator.valueOf(routingIndicator);
    // this.sccpAddress = new SccpAddressImpl(ri,gt,dpc,ssn)
    this.name = name;
    this.routingIndicator = ri;
    this.digits = digits;
  }

  /**
   * Add a Routing Address
   * 
   * @param id               The id for the routing address
   * @param routingIndicator The routing indicator
   * @param gt               The GlobalTitle Object
   * @param dpc              An Integer for the point code
   * @param ssn              An Integer for the subsystem number
   * @deprecated Old implementation. use the new implementation
   */
  public SccpRoutingAddress(int id, String routingIndicator, GlobalTitle gt, int dpc, int ssn) {
    this.id = id;
    RoutingIndicator ri = RoutingIndicator.valueOf(routingIndicator);
    SccpAddress sccpAddress = new SccpAddressImpl(ri, gt, dpc, ssn);
    sccpAddresses.add(sccpAddress);
  }

  public String getName() {
    return this.name;
  }

  public RoutingIndicator getRoutingIndicator() {
    return this.routingIndicator;
  }

  public int getId() {
    return this.id;
  }

  public String getDigits() {
    return this.digits;
  }

  public SccpAddress getSccpAddress() {
    // limiting the sccpaddress for the routing address to only one. fix this if multiple are
    // allowed
    if (this.sccpAddresses.size() > 0) {
      return this.sccpAddresses.get(0);
    }
    return null;
  }

  public void addSccpAddress(SccpAddress sccpAddress) {
    this.sccpAddresses.add(sccpAddress);
  }

  /**
   * Convert the parameters to SccpAddress
   * 
   * @param routingIndicator The Routing Indicator should be a string value which is converted to
   *                         enum value
   * @param dpc              The destination point code
   * @param ssn              The subsystem number
   * @param type             The type of the sccp accress
   * @param translationType  The tranlation Type
   * @param encodingScheme   The encoding scheme. 1 = BCDOddEncodingScheme, 2 =
   *                         BCDEvenEncodingScheme. Anything else is DefaultEncodingScheme
   * @param numberingPlan    The numbering plan
   * @param natureofAddress  the Nature of Address
   * @param digit            The digits
   * @return SccpAddress
   */
  public static SccpAddress convertToSccpAddress(String routingIndicator, int dpc,
      int ssn, String type, int translationType, int encodingScheme, int numberingPlan,
      String natureofAddress, String digit) {
    // create the gt from the parameters above
    RoutingIndicator ri = RoutingIndicator.valueOf(routingIndicator);
    ParameterFactoryImpl factory = new ParameterFactoryImpl();
    EncodingScheme ec;
    switch (encodingScheme) {
      case 1:
        ec = new BCDOddEncodingScheme();
        break;
      case 2:
        ec = new BCDEvenEncodingScheme();
        break;
      default:
        ec = new DefaultEncodingScheme();
        break;
    }
    // NumberingPlan
    NumberingPlan np = NumberingPlan.valueOf(numberingPlan);
    // NatureOfAddress
    NatureOfAddress noa = NatureOfAddress.valueOf(natureofAddress);
    // String digits, int translationType, NumberingPlan numberingPlan,
    // EncodingScheme encodingScheme, NatureOfAddress noa
    GlobalTitle gt = factory.createGlobalTitle(digit, translationType, np, ec, noa);

    // routingAddressMap.put(name, new SccpRoutingAddress(id, routingIndicator, GT,
    // dpc, ssn));
    SccpAddress retCccpAddress = new SccpAddressImpl(ri, gt, dpc, ssn);
    
    return retCccpAddress;

  }
}
