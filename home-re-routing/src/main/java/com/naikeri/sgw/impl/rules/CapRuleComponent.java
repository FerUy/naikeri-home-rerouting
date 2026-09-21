package com.naikeri.sgw.impl.rules;

import java.util.ArrayList;
import java.util.List;

/**
 * CapRuleComponent
 */
public class CapRuleComponent {

  /**
   * Remove
   */
  public static class Remove {
    private final List<String> primitives = new ArrayList<>();

    public List<String> getPrimitives() {
      return primitives;
    }

    public void setPrimitives(String primitives) {
      for (String primitive : primitives.split(",")) {
        this.primitives.add(primitive);
      }
    }

  }
  /**
   * Replace
   */
  public static class Replace {

    /**
     * ReplaceArguments
     */
    public static class ReplaceArguments {

      private String primitives;
      private String cdPN;
      private String range;
      private int nai;
      private int npi;
      private int inni = 0;

      public String getPrimitives() {
        return primitives;
      }

      public void setPrimitives(String primitives) {
        this.primitives = primitives;
      }

      public int getNai() {
        return nai;
      }

      // use international by default if there is not match from the configuration file
      public void setNai(String nai) {
        int tempNumber = CAPNaiNumber.INTERNATIONAL.getValue();
        try {
          tempNumber = CAPNaiNumber.valueOf(nai).getValue();
        } catch (Exception e) {
          //
        }
        this.nai = tempNumber;
      }

      public int getNpi() {
        return npi;
      }

      public void setNpi(String npi) {
        int tempNumber = CAPNumberingPlanIndicator.ISDN.getValue();
        try {
          tempNumber = CAPNumberingPlanIndicator.valueOf(npi).getValue();
        } catch (Exception e) {
          //
        }
        this.npi = tempNumber;
      }

      public int getInni() {
        return inni;
      }

      public void setInni(String inni) {
        if (inni.equalsIgnoreCase("ALLOWED")) {
          this.inni = 0;
        } else {
          // int _INN_ROUTING_NOT_ALLOWED = 1
          this.inni = 1;
        }
      }

      public String getCdPN() {
        return cdPN;
      }

      public void setCdPN(String cdPN) {
        this.cdPN = cdPN;
      }

      public String getRange() {
        return range;
      }

      public void setRange(String range) {
        this.range = range;
      }
    }

    private String primitive;
    private boolean apply;
    private ReplaceArguments arguments;

    public String getPrimitive() {
      return primitive;
    }

    public void setPrimitive(String primitive) {
      this.primitive = primitive;
    }

    public boolean getApply() {
      return apply;
    }

    public void setApply(boolean apply) {
      this.apply = apply;
    }

    public ReplaceArguments getArgument() {
      return arguments;
    }

    public void setArguments(ReplaceArguments arguments) {
      this.arguments = arguments;
    }
  }

  private Remove remove;
  private Replace replace;

  public Remove getRemove() {
    return remove;
  }

  public void setRemove(Remove remove) {
    this.remove = remove;
  }

  public Replace getReplace() {
    return replace;
  }

  public void setReplace(Replace replace) {
    this.replace = replace;
  }
}
