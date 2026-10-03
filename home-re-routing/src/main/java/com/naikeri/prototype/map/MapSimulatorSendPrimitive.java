package com.naikeri.prototype.map;

import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import com.google.common.util.concurrent.RateLimiter;
import com.naikeri.sgw.impl.settings.sccp.SccpSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobicents.protocols.asn.BitSetStrictLength;
import org.restcomm.protocols.ss7.map.MAPStackImpl;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContext;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextName;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParameterFactory;
import org.restcomm.protocols.ss7.map.api.MAPProvider;
import org.restcomm.protocols.ss7.map.api.primitives.*;
import org.restcomm.protocols.ss7.map.api.service.callhandling.MAPDialogCallHandling;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientExternalID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientInternalID;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.ReSynchronisationInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.RequestingNodeType;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.UEUsageType;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.ResetId;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.ADDInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.AgeIndicator;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.EPSInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.ExtSupportedFeatures;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.ISTSupportIndicator;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.LocationArea;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.NetworkNodeDiameterAddress;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.PagingArea;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SGSNCapability;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SMSRegisterRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SuperChargerInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedFeatures;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedLCSCapabilitySets;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedRATTypes;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UESRVCCCapability;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.VLRCapability;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LIPAPermission;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.PDPContext;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SIPTOPermission;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.AMBR;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.APN;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.APNConfiguration;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.APNConfigurationProfile;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.APNOIReplacement;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.AccessRestrictionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.AdditionalInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.AdditionalSubscriptions;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.AdjacentAccessRestrictionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.AllocationRetentionPriority;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.BearerServiceCodeValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CSAllocationRetentionPriority;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CSGId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CSGSubscriptionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CallTypeCriteria;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.Category;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CategoryValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CauseValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CauseValueCodeValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ChargingCharacteristics;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DCSI;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DPAnalysedInfoCriterium;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DefaultCallHandling;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DefaultGPRSHandling;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DefaultSMSHandling;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DestinationNumberCriteria;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.EDRXCycleLength;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.EDRXCycleLengthValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.EPSQoSSubscribed;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.EPSSubscriptionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.Ext2QoSSubscribed;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.Ext2QoSSubscribed_SourceStatisticsDescriptor;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.Ext3QoSSubscribed;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.Ext4QoSSubscribed;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtAccessRestrictionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBasicServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBearerServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtPDPType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtQoSSubscribed;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtQoSSubscribed_BitRate;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtQoSSubscribed_BitRateExtended;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtQoSSubscribed_DeliveryOfErroneousSdus;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtQoSSubscribed_DeliveryOrder;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtQoSSubscribed_MaximumSduSize;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtQoSSubscribed_ResidualBER;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtQoSSubscribed_SduErrorRatio;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtQoSSubscribed_TrafficClass;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtQoSSubscribed_TrafficHandlingPriority;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtQoSSubscribed_TransferDelay;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtSSData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtSSInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtSSStatus;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtTeleserviceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExternalClient;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.GMLCRestriction;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.GPRSCSI;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.GPRSCamelTDPData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.GPRSSubscriptionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.GPRSTriggerDetectionPoint;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.GroupId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.IMSIGroupId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LCSInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LCSPrivacyClass;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAAttributes;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAIdentificationPriorityValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAIdentity;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAOnlyAccessIndicator;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LocalGroupId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LongGroupId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.MCSI;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.MCSSInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.MGCSI;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.MMCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.MMCodeValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.MOLRClass;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.MTSMSTPDUType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.MTsmsCAMELTDPCriteria;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.MatchType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.NetworkAccessMode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.NotificationToMSUser;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OBcsmCamelTDPData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OBcsmCamelTdpCriteria;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OBcsmTriggerDetectionPoint;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OCSI;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ODBData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ODBGeneralData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ODBHPLMNData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OfferedCamel4CSIs;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.PDNGWAllocationType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.PDNGWIdentity;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.PDNType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.PDNTypeValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.PDPAddress;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.PDPType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.PDPTypeValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.QoSClassIdentifier;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.QoSSubscribed;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.QoSSubscribed_DelayClass;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.QoSSubscribed_MeanThroughput;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.QoSSubscribed_PeakThroughput;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.QoSSubscribed_PrecedenceClass;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.QoSSubscribed_ReliabilityClass;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SGSNCAMELSubscriptionInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SMSCAMELTDPData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SMSCSI;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SMSTriggerDetectionPoint;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SSCSI;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SSCamelData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ServiceType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SpecificAPNInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SubscriberStatus;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SupportedCamelPhases;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.TBcsmCamelTDPData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.TBcsmCamelTdpCriteria;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.TBcsmTriggerDetectionPoint;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.TCSI;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.TeleserviceCodeValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.VlrCamelSubscriptionInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.VoiceBroadcastData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.VoiceGroupCallData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ZoneCode;
import org.restcomm.protocols.ss7.map.api.service.sms.*;
import org.restcomm.protocols.ss7.map.api.service.supplementary.CliRestrictionOption;
import org.restcomm.protocols.ss7.map.api.service.supplementary.OverrideCategory;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSSubscriptionOption;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SupplementaryCodeValue;
import org.restcomm.protocols.ss7.map.api.smstpdu.AbsoluteTimeStamp;
import org.restcomm.protocols.ss7.map.api.smstpdu.AddressField;
import org.restcomm.protocols.ss7.map.api.smstpdu.CharacterSet;
import org.restcomm.protocols.ss7.map.api.smstpdu.DataCodingScheme;
import org.restcomm.protocols.ss7.map.api.smstpdu.NumberingPlanIdentification;
import org.restcomm.protocols.ss7.map.api.smstpdu.ProtocolIdentifier;
import org.restcomm.protocols.ss7.map.api.smstpdu.SmsDeliverTpdu;
import org.restcomm.protocols.ss7.map.api.smstpdu.TypeOfNumber;
import org.restcomm.protocols.ss7.map.api.smstpdu.UserData;
import org.restcomm.protocols.ss7.map.api.smstpdu.UserDataHeader;
import org.restcomm.protocols.ss7.map.api.smstpdu.ValidityPeriod;
import org.restcomm.protocols.ss7.map.primitives.DiameterIdentityImpl;
import org.restcomm.protocols.ss7.map.primitives.NAEACICImpl;
import org.restcomm.protocols.ss7.map.primitives.NAEAPreferredCIImpl;
import org.restcomm.protocols.ss7.map.primitives.TimeImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.GSNAddressImpl;
import org.restcomm.protocols.ss7.map.primitives.IMEIImpl;
import org.restcomm.protocols.ss7.map.primitives.PlmnIdImpl;
import org.restcomm.protocols.ss7.map.primitives.LMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.SignalInfoImpl;
import org.restcomm.protocols.ss7.map.primitives.ExternalSignalInfoImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSClientExternalIDImpl;
import org.restcomm.protocols.ss7.map.service.mobility.authentication.ReSynchronisationInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.authentication.UEUsageTypeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.faultRecovery.ResetIdImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.NetworkNodeDiameterAddressImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.PagingAreaImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SuperChargerInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SupportedLCSCapabilitySetsImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SupportedRATTypesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.ExtSupportedFeaturesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SGSNCapabilityImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.ADDInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.EPSInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.LACImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.ISRInformationImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.LocationAreaImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SupportedFeaturesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.VLRCapabilityImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.PDPContextImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.AMBRImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.APNConfigurationImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.APNConfigurationProfileImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.APNImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.APNOIReplacementImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.AccessRestrictionDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.AdditionalInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.AdditionalSubscriptionsImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.AdjacentAccessRestrictionDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.AgeIndicatorImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.AllocationRetentionPriorityImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.CSAllocationRetentionPriorityImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.CSGIdImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.CSGSubscriptionDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.CategoryImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.CauseValueImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ChargingCharacteristicsImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.DCSIImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.DPAnalysedInfoCriteriumImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.DestinationNumberCriteriaImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.EDRXCycleLengthImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.EDRXCycleLengthValueImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.EPSQoSSubscribedImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.EPSSubscriptionDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.Ext2QoSSubscribedImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.Ext3QoSSubscribedImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.Ext4QoSSubscribedImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtAccessRestrictionDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtBearerServiceCodeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtPDPTypeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtQoSSubscribedImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtQoSSubscribed_BitRateExtendedImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtQoSSubscribed_BitRateImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtQoSSubscribed_MaximumSduSizeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtQoSSubscribed_TransferDelayImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtSSDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtSSInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtSSStatusImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtTeleserviceCodeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExternalClientImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.GPRSCSIImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.GPRSCamelTDPDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.GPRSSubscriptionDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.GroupIdImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.IMSIGroupIdImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.LCSInformationImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.LCSPrivacyClassImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.LSAAttributesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.LSADataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.LSAIdentityImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.LSAInformationImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.LocalGroupIdImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.LongGroupIdImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.MCSIImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.MCSSInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.MGCSIImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.MMCodeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.MOLRClassImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.MTsmsCAMELTDPCriteriaImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.OBcsmCamelTDPDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.OBcsmCamelTdpCriteriaImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.OCSIImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ODBDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ODBGeneralDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ODBHPLMNDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.OfferedCamel4CSIsImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.PDNTypeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.PDPAddressImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.PDPTypeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.QoSSubscribedImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SGSNCAMELSubscriptionInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SMSCAMELTDPDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SMSCSIImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SSCSIImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SSCamelDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ServiceTypeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SupportedCamelPhasesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.TBcsmCamelTDPDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.TBcsmCamelTdpCriteriaImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.TCSIImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.VlrCamelSubscriptionInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.VoiceBroadcastDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.VoiceGroupCallDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ZoneCodeImpl;
import org.restcomm.protocols.ss7.map.service.sms.CorrelationIDImpl;
import org.restcomm.protocols.ss7.map.service.sms.SM_RP_OAImpl;
import org.restcomm.protocols.ss7.map.service.sms.SipUriImpl;
import org.restcomm.protocols.ss7.map.service.sms.SmsSignalInfoImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.SSCodeImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.SSSubscriptionOptionImpl;
import org.restcomm.protocols.ss7.map.smstpdu.AbsoluteTimeStampImpl;
import org.restcomm.protocols.ss7.map.smstpdu.AddressFieldImpl;
import org.restcomm.protocols.ss7.map.smstpdu.ApplicationPortAddressing16BitAddressImpl;
import org.restcomm.protocols.ss7.map.smstpdu.DataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.smstpdu.ProtocolIdentifierImpl;
import org.restcomm.protocols.ss7.map.smstpdu.SmsDeliverTpduImpl;
import org.restcomm.protocols.ss7.map.smstpdu.SmsSubmitTpduImpl;
import org.restcomm.protocols.ss7.map.smstpdu.SmsTpduImpl;
import org.restcomm.protocols.ss7.map.smstpdu.UserDataHeaderImpl;
import org.restcomm.protocols.ss7.map.smstpdu.UserDataImpl;
import org.restcomm.protocols.ss7.map.smstpdu.ValidityPeriodImpl;
import org.restcomm.protocols.ss7.sccp.NetworkIdState;

public class MapSimulatorSendPrimitive {

  private RateLimiter rateLimiterObj;
  private static final Logger logger = LogManager.getLogger(MapSimulatorSendPrimitive.class.getName());

  private final SccpSettings sccpClientSettings;
  private final MAPStackImpl mapClient;
  private MAPParameterFactory mapParameterFactory;

  public MapSimulatorSendPrimitive(MAPStackImpl map, SccpSettings sccpClientSettings) {
    this.mapClient = map;
    this.sccpClientSettings = sccpClientSettings;
    this.mapParameterFactory = map.getMAPProvider().getMAPParameterFactory();
    this.rateLimiterObj = RateLimiter.create(5000);
  }

  // The simulator's addresses by role, as named in map-simulator-config.xml's sccpclient
  private SccpAddress address(String name) {
    return this.sccpClientSettings.getRoutingAddresses().stream()
        .filter(a -> name.equals(a.getName()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No routing address '" + name + "' in sccpclient"))
        .getSccpAddress();
  }

  public void sendMtForwardSM(String imsiString) {
    try {
      logger.debug("Sending MAP MT-FORWARD-SM for IMSI = {}", imsiString);
      this.mapClient.getMAPProvider().getMAPServiceSms().activate();

      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control. Simulator test for MAP MT-FSM: networkIdState={}", networkIdState);
        Thread.sleep(3000);
      }

      this.rateLimiterObj.acquire();

      MAPApplicationContext appCnt;
      appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.shortMsgMTRelayContext, MAPApplicationContextVersion.version3);
      AddressString originReference = this.mapParameterFactory.createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "31628968300");
      AddressString destReference = this.mapParameterFactory.createAddressString(AddressNature.international_number, NumberingPlan.land_mobile, "204208300008002");

      MAPDialogSms clientDialogSms =
          this.mapClient.getMAPProvider().getMAPServiceSms().createNewDialog(appCnt,
              address("hplmnSmsc"), originReference,
              address("toVplmn"), destReference);
      SM_RP_DA smRPDA = this.mapParameterFactory.createSM_RP_DA(this.mapParameterFactory.createIMSI(imsiString));
      AddressString msisdn1 = this.mapParameterFactory
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "111222333");
      SM_RP_OA smRPOA = this.mapParameterFactory.createSM_RP_OA_ServiceCentreAddressOA(msisdn1);
      AddressField originatingAddress = new AddressFieldImpl(TypeOfNumber.Alphanumeric, NumberingPlanIdentification.Unknown, "447");
      ZonedDateTime now = ZonedDateTime.now();
      AbsoluteTimeStamp serviceCentreTimeStamp = getAbsoluteTimeStamp(now);
      int dcsVal = 4; // 0 = GSM7, 4 = GSM8, 8 = UCS2
      DataCodingScheme dcs = new DataCodingSchemeImpl(dcsVal);
      UserDataHeader udh = null;
      if (dcs.getCharacterSet() == CharacterSet.GSM8) {
        ApplicationPortAddressing16BitAddressImpl apa16 = new ApplicationPortAddressing16BitAddressImpl(16020, 0);
        udh = new UserDataHeaderImpl();
        udh.addInformationElement(apa16);
      }
      boolean moreMessagesToSend = false;
      boolean forwardedOrSpawned = false;
      boolean replyPathExists = false;
      boolean statusReportIndication = false;
      Charset gsm8Charset = StandardCharsets.UTF_8;
      UserData userData = new UserDataImpl("MT-SMS text", dcs, udh, gsm8Charset);
      ProtocolIdentifier pi = new ProtocolIdentifierImpl(0);

      Integer smDeliveryTimer = 60;
      boolean smsOverIPOnlyIndicator = true;
      DiameterIdentity gmscName = new DiameterIdentityImpl("msc04.mme.epc.mnc002.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8));
      DiameterIdentity gmscRealm = new DiameterIdentityImpl("epc.mnc002.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8));
      SmsDeliverTpdu tpdu = new SmsDeliverTpduImpl(moreMessagesToSend, forwardedOrSpawned, replyPathExists, statusReportIndication, originatingAddress, pi, serviceCentreTimeStamp, userData);
      SmsSignalInfo sm_RP_UI = new SmsSignalInfoImpl(tpdu, gsm8Charset);
      ZonedDateTime utcNow = now.withZoneSameInstant(ZoneOffset.UTC);
      ZonedDateTime utcDeadline = utcNow.plusDays(1);
      Time smDeliveryStartTime = new TimeImpl(utcNow.getYear(), utcNow.getMonthValue(), utcNow.getDayOfMonth(),
              utcNow.getHour(), utcNow.getMinute(), utcNow.getSecond());
      Time maximumRetransmissionTime = new TimeImpl(utcDeadline.getYear(), utcDeadline.getMonthValue(),
              utcDeadline.getDayOfMonth(), utcDeadline.getHour(), utcDeadline.getMinute(), utcDeadline.getSecond());
      ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number,
              NumberingPlan.ISDN, "59899077937");
      String uriA = msisdn.getAddress() + "@restcomm.org";
      SipUri sipUriA = new SipUriImpl(uriA.getBytes(StandardCharsets.UTF_8));
      SipUri sipUriB = new SipUriImpl("mtLoadTest@restcomm.org".getBytes(StandardCharsets.UTF_8));
      CorrelationID correlationID = new CorrelationIDImpl(new IMSIImpl(imsiString), sipUriA, sipUriB);
      ISDNAddressString smsGmscAddress = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "5989900123");
      NetworkNodeDiameterAddress smsGmscDiameterAddress = new NetworkNodeDiameterAddressImpl(gmscName, gmscRealm);

      clientDialogSms.addMtForwardShortMessageRequest(smRPDA, smRPOA, sm_RP_UI, moreMessagesToSend, null, smDeliveryTimer,
              smDeliveryStartTime, smsOverIPOnlyIndicator, correlationID, maximumRetransmissionTime, smsGmscAddress, smsGmscDiameterAddress);

      clientDialogSms.send();
      logger.debug("Message sent successfully");
    } catch (Exception e) {
      logger.error("Error occurred", e);
    }
  }

  private static AbsoluteTimeStamp getAbsoluteTimeStamp(ZonedDateTime zonedDateTime) {
    int quarterHours = zonedDateTime.getOffset().getTotalSeconds() / 900;
    return new AbsoluteTimeStampImpl(zonedDateTime.getYear() - 2000, zonedDateTime.getMonthValue(), zonedDateTime.getDayOfMonth(),
            zonedDateTime.getHour(), zonedDateTime.getMinute(), zonedDateTime.getSecond(), quarterHours);
  }

  public void sendMoForwardSm(String imsiString) {
    try {
      this.mapClient.getMAPProvider().getMAPServiceSms().activate();
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control. Simulator test for MAP MO-FSM: networkIdState={}", networkIdState);
        Thread.sleep(3000);
      }

      this.rateLimiterObj.acquire();
      MAPApplicationContext appCnt;

      appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.shortMsgMORelayContext, MAPApplicationContextVersion.version3);

      AddressString originReference = this.mapParameterFactory.createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "31628968300");
      AddressString destReference = this.mapParameterFactory.createAddressString(AddressNature.international_number, NumberingPlan.land_mobile, "204208300008002");

      MAPDialogSms clientDialogSms = this.mapClient.getMAPProvider().getMAPServiceSms().createNewDialog(appCnt,
              address("vplmn"), originReference,
              address("toHplmnSmsc"), destReference);
      // clientDialogSms.setExtentionContainer(MAPExtensionContainerTest.GetTestExtensionContainer())

      // MO-ForwardSM names the service centre in sm-RP-DA; the subscriber's IMSI travels in the imsi field.
      AddressString serviceCentreAddress = this.mapParameterFactory.createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "111222333");
      SM_RP_DA smRPDA = this.mapParameterFactory.createSM_RP_DA(serviceCentreAddress);
      IMSI imsi = this.mapParameterFactory.createIMSI(imsiString);
      ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "31628838002");
      SM_RP_OAImpl sm_rp_oa = new SM_RP_OAImpl();
      sm_rp_oa.setMsisdn(msisdn);
      boolean rejectDuplicates = true;
      boolean replyPathExists = false;
      boolean statusReportRequest = true;
      int messageReference = 187;
      AddressField destinationAddress = new AddressFieldImpl(TypeOfNumber.InternationalNumber, NumberingPlanIdentification.ISDNTelephoneNumberingPlan, "59899077937");
      ProtocolIdentifier protocolIdentifier = new ProtocolIdentifierImpl(0);
      ValidityPeriod validityPeriod = new ValidityPeriodImpl(3);
      DataCodingScheme dataCodingScheme = new DataCodingSchemeImpl(0);
      UserDataHeader userDataHeader = null;
      if (dataCodingScheme.getCharacterSet() == CharacterSet.GSM8) {
        ApplicationPortAddressing16BitAddressImpl apa16 = new ApplicationPortAddressing16BitAddressImpl(16020, 0);
        userDataHeader = new UserDataHeaderImpl();
        userDataHeader.addInformationElement(apa16);
      }
      Charset gsm8Charset = StandardCharsets.UTF_8;
      UserData userData = new UserDataImpl("MO SMS test", dataCodingScheme, userDataHeader, gsm8Charset);
      SmsTpduImpl smsTpdu = new SmsSubmitTpduImpl(rejectDuplicates, replyPathExists, statusReportRequest, messageReference, destinationAddress,
          protocolIdentifier, validityPeriod, userData);
      SmsSignalInfo sm_rp_ui = new SmsSignalInfoImpl(smsTpdu, gsm8Charset);
      // In the  MSISDN-less SMS in IMS case the originating user is identified by SIP-URI-A of the correlationID parameter.
      IMSI hlrId = new IMSIImpl(imsiString);
      SipUri sipUriA = new SipUriImpl("sip:kbza@acme.com".getBytes(StandardCharsets.UTF_8));
      SipUri sipUriB = new SipUriImpl("sip:fer@restcomm.org".getBytes(StandardCharsets.UTF_8));
      CorrelationID correlationID = new CorrelationIDImpl(hlrId, sipUriA, sipUriB);
      SMDeliveryOutcome smDeliveryOutcome = SMDeliveryOutcome.absentSubscriber;

      clientDialogSms.addMoForwardShortMessageRequest(smRPDA, sm_rp_oa, sm_rp_ui, null, imsi, correlationID, smDeliveryOutcome);

      clientDialogSms.send();
    } catch (Exception e) {
      logger.error("Error while sending MAP MO-Forward-Short-Message", e);
    }
  }

  public void initiateUpdateGprsLocation(String imsiString, String sgsnAddressString, String sgsnNumberString) {
    try {
      logger.info("Sending MAP UPDATE-GPRS-LOCATION Request");
      MAPProvider clientMapProvider = this.mapClient.getMAPProvider();
      // preparing congestion control -- code not changing
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control. Simulator test for MAP UGL: networkIdState={}", networkIdState);
        Thread.sleep(3000);
      }

      this.rateLimiterObj.acquire();
      ///
      // create origination and destination address references
      AddressString origRef = clientMapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "12345");
      AddressString destRef = clientMapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "67890");
      // sending a message
      MAPDialogMobility mapDialogMobility =
          clientMapProvider.getMAPServiceMobility().createNewDialog(
              MAPApplicationContext.getInstance(MAPApplicationContextName.gprsLocationUpdateContext,
                  MAPApplicationContextVersion.version3),
              address("vplmn"), origRef,
              address("toHplmnHlr"), destRef);

      ISDNAddressString sgsnNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, sgsnNumberString);
      IMSI imsi = new IMSIImpl(imsiString);
      byte[] sgsnAddressByteArray = new BigInteger(sgsnAddressString, 16).toByteArray();
      GSNAddress sgsnAddress = new GSNAddressImpl(sgsnAddressByteArray);
      boolean solsaSupportIndicator = false;
      Boolean sendSubscriberData = true;
      SuperChargerInfo superChargerSupportedInServingNetworkEntity = new SuperChargerInfoImpl(sendSubscriberData);
      boolean gprsEnhancementsSupportIndicator = true;
      SupportedCamelPhases supportedCamelPhases = new SupportedCamelPhasesImpl(true, true, true, false);
      SupportedLCSCapabilitySets supportedLCSCapabilitySets = new SupportedLCSCapabilitySetsImpl(true, true, true, true, false);
      boolean oCsi = false;
      boolean dCsi = false;
      boolean vtCsi = false;
      boolean tCsi = false;
      boolean mtSMSCsi = true;
      boolean mgCsi = true;
      boolean psiEnhancements = true;
      OfferedCamel4CSIs offeredCamel4CSIs = new OfferedCamel4CSIsImpl(oCsi,dCsi,vtCsi,tCsi, mtSMSCsi, mgCsi, psiEnhancements);
      boolean smsCallBarringSupportIndicator = true;
      boolean utran = true;
      boolean geran = true;
      boolean gan = false;
      boolean i_hspa_evolution = true;
      boolean e_utran = true;
      boolean nb_iot = true;
      SupportedRATTypes supportedRATTypesIndicator = new SupportedRATTypesImpl(utran, geran, gan, i_hspa_evolution, e_utran, nb_iot);
      SupportedFeatures supportedFeatures = getSupportedFeatures();
      boolean tAdsDataRetrieval = true;
      Boolean homogeneousSupportOfIMSVoiceOverPSSessions = true;
      boolean cancellationTypeInitialAttach = true;
      boolean msisdnlessOperationSupported = true;
      boolean updateOfHomogeneousSupportOfIMSVoiceOverPSSessions = true;
      boolean resetIdsSupported = true;
      boolean unlicensedSpectrumAsSecondaryRAT = true;
      ExtSupportedFeatures extSupportedFeatures = new ExtSupportedFeaturesImpl(unlicensedSpectrumAsSecondaryRAT);
      SGSNCapability sgsnCapability = new SGSNCapabilityImpl(solsaSupportIndicator, null,
          superChargerSupportedInServingNetworkEntity, gprsEnhancementsSupportIndicator, supportedCamelPhases,
          supportedLCSCapabilitySets, offeredCamel4CSIs, smsCallBarringSupportIndicator, supportedRATTypesIndicator,
          supportedFeatures, tAdsDataRetrieval, homogeneousSupportOfIMSVoiceOverPSSessions, cancellationTypeInitialAttach,
          msisdnlessOperationSupported, updateOfHomogeneousSupportOfIMSVoiceOverPSSessions, resetIdsSupported,
          extSupportedFeatures);
      boolean informPreviousNetworkEntity = true;
      boolean psLCSNotSupportedByUE = false;
      GSNAddress vGmlcAddress = new GSNAddressImpl(new byte[] { 23, 5, 38, 48, 81, 5 });
      boolean skipSubscriberDataUpdate = false;
      ADDInfo addInfo = new ADDInfoImpl(new IMEIImpl("356024081653200"), skipSubscriberDataUpdate);
      boolean updateMME = true;
      boolean cancelSGSN = true;
      boolean initialAttachIndicator = true;
      EPSInfo epsInfo = new EPSInfoImpl(new ISRInformationImpl(updateMME, cancelSGSN, initialAttachIndicator));
      boolean servingNodeTypeIndicator = true;
      UsedRATType usedRATType = UsedRATType.utran;
      boolean gprsSubscriptionDataNotNeeded = true;
      boolean nodeTypeIndicator = true;
      boolean areaRestricted = true;
      boolean ueReachableIndicator = true;
      boolean epsSubscriptionDataNotNeeded = true;
      UESRVCCCapability uesrvccCapability = UESRVCCCapability.ueSrvccSupported;
      ArrayList<PlmnId> ePLMNList = new ArrayList<>();
      PlmnId plmnId1 = new PlmnIdImpl(262,1);
      PlmnId plmnId2 = new PlmnIdImpl(262,999);
      ePLMNList.add(plmnId1);
      ePLMNList.add(plmnId2);
      ISDNAddressString mmeNumberForMTSMS = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "491710490001");
      SMSRegisterRequest smsRegisterRequest = SMSRegisterRequest.isNoPreference;
      boolean smsOnly = true;
      byte[] sgsnNameArray = "mme.20.mag.epc.mnc001.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8);
      DiameterIdentity sgsnName = new DiameterIdentityImpl(sgsnNameArray);
      byte[] sgsnRealmArray = "epc.mnc001.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8);
      DiameterIdentity sgsnRealm = new DiameterIdentityImpl(sgsnRealmArray);
      boolean lgdSupportIndicator = false;
      boolean removalOfMMERegistrationForSMS = false;
      ArrayList<PlmnId> adjacentPLMNList = new ArrayList<>();
      PlmnId adjPlmnId1 = new PlmnIdImpl(262,2);
      PlmnId adjPlmnId2 = new PlmnIdImpl(262,3);
      adjacentPLMNList.add(adjPlmnId1);
      adjacentPLMNList.add(adjPlmnId2);

      Long invokeId = mapDialogMobility.addUpdateGprsLocationRequest(imsi, sgsnNumber, sgsnAddress,
          null, sgsnCapability, informPreviousNetworkEntity, psLCSNotSupportedByUE,
          vGmlcAddress, addInfo, epsInfo, servingNodeTypeIndicator, skipSubscriberDataUpdate,
          usedRATType, gprsSubscriptionDataNotNeeded, nodeTypeIndicator, areaRestricted,
          ueReachableIndicator, epsSubscriptionDataNotNeeded, uesrvccCapability, ePLMNList,
          mmeNumberForMTSMS, smsRegisterRequest, smsOnly, sgsnName, sgsnRealm, lgdSupportIndicator,
          removalOfMMERegistrationForSMS, adjacentPLMNList);
      logger.info("InvokeId = {}", invokeId);
      mapDialogMobility.send();
    } catch (Exception e) {
      logger.error("Error while sending MAP Update-GPRS-Location", e);
    }
  }

  public void simulateUpdateLocationRequest(String imsiString) {
    try {
      logger.info("Sending MAP UPDATE-LOCATION Request");
      MAPProvider clientMapProvider = this.mapClient.getMAPProvider();
      // preparing congestion control -- code not changing
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control. Simulator test for MAP UL: networkIdState={}", networkIdState);

        Thread.sleep(3000);

      }

      this.rateLimiterObj.acquire();
      clientMapProvider.getMAPServiceMobility().activate();

      MAPApplicationContext appCnt;

      appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.networkLocUpContext,
          MAPApplicationContextVersion.version3);

      MAPDialogMobility clientDialogMobility =
          clientMapProvider.getMAPServiceMobility().createNewDialog(appCnt,
              address("vplmn"), null,
              address("toHplmnHlr"), null);

      IMSI imsi = this.mapParameterFactory.createIMSI(imsiString);
      ISDNAddressString mscNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "491710460000");
      ISDNAddressString roamingNumber = null;
      ISDNAddressString vlrNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "491710460000");
      LMSI lmsi = null;
      VLRCapability vlrCapability = getVlrCapability();
      boolean informPreviousNetworkEntity = false;
      boolean csLCSNotSupportedByUE = false;
      GSNAddress vGmlcAddress = null;
      ADDInfo addInfo = null;
      PagingArea pagingArea = null;
      boolean skipSubscriberDataUpdate = false;
      boolean restorationIndicator = false;
      ArrayList<PlmnId> ePLMNList = new ArrayList<>();
      PlmnId plmnId1 = new PlmnIdImpl(262,1);
      PlmnId plmnId2 = new PlmnIdImpl(262,999);
      ePLMNList.add(plmnId1);
      ePLMNList.add(plmnId2);
      NetworkNodeDiameterAddress mmeDiameterAddress = getNetworkNodeDiameterAddress();
      clientDialogMobility.addUpdateLocationRequest(imsi, mscNumber, roamingNumber, vlrNumber, lmsi, null,
          vlrCapability, informPreviousNetworkEntity, csLCSNotSupportedByUE, vGmlcAddress, addInfo, pagingArea,
          skipSubscriberDataUpdate, restorationIndicator, ePLMNList, mmeDiameterAddress);

      clientDialogMobility.send();
    } catch (Exception e) {
      logger.error("Error while sending MAP Update-Location", e);
    }
  }

  public void sendAuthenticationInfo(String imsiString) {
    try {
      logger.info("Sending MAP SEND-AUTHENTICATION-INFO Request");

      MAPProvider clientMapProvider = this.mapClient.getMAPProvider();
      // preparing congestion control -- code not changing
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null
          || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control. Simulator test for MAP SAI: networkIdState={}", networkIdState);
        Thread.sleep(3000);
      }

      this.rateLimiterObj.acquire();
      clientMapProvider.getMAPServiceMobility().activate();

      MAPApplicationContext appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.infoRetrievalContext, MAPApplicationContextVersion.version3);

      MAPDialogMobility clientDialogMobility =
          clientMapProvider.getMAPServiceMobility().createNewDialog(appCnt,
              address("vplmn"), null,
              address("toHplmnHlr"), null);

      IMSI imsi = this.mapParameterFactory.createIMSI(imsiString);

      int numberOfRequestedVectors = 5;
      boolean segmentationProhibited = false;
      boolean immediateResponsePreferred = false;
      ReSynchronisationInfo reSynchronisationInfo = getReSynchronisationInfo();
      RequestingNodeType requestingNodeType = RequestingNodeType.vlr;
      byte[] mccMnc = new byte[] {0x47, (byte) 0xf8, 0x10};
      PlmnId requestingPlmnId = new PlmnIdImpl(mccMnc);
      Integer numberOfRequestedAdditionalVectors = 1;
      boolean additionalVectorsAreForEPS = true;
      boolean ueUsageTypeRequestIndication = true;

      clientDialogMobility.addSendAuthenticationInfoRequest(imsi, numberOfRequestedVectors, segmentationProhibited,
          immediateResponsePreferred, reSynchronisationInfo, null, requestingNodeType, requestingPlmnId,
          numberOfRequestedAdditionalVectors, additionalVectorsAreForEPS, ueUsageTypeRequestIndication);
      clientDialogMobility.send();

    } catch (Exception e) {
      logger.error("Error while sending MAP Send-Authentication-Info", e);
    }
  }

  public void initiateProvideRoamingNumber(String imsiString) throws MAPException {
    try {
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      MAPProvider clientMapProvider = this.mapClient.getMAPProvider();
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control. Simulator test for MAP PRN: networkIdState={}", networkIdState);

        Thread.sleep(3000);

      }
      this.rateLimiterObj.acquire();
      // First create Dialog
      AddressString origRef = clientMapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "12345");
      AddressString destRef = clientMapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "67890");

      clientMapProvider.getMAPServiceCallHandling().activate();
      MAPApplicationContext appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.roamingNumberEnquiryContext,
          MAPApplicationContextVersion.version3);

      MAPDialogCallHandling mapDialogMobility = clientMapProvider.getMAPServiceCallHandling().createNewDialog(appCnt,
              address("hplmnHlr"), origRef,
              address("toVplmn"), destRef);
      IMSI imsi = new IMSIImpl(imsiString);
      ISDNAddressString mscNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "22228");
      ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "22227");
      LMSI lmsi = new LMSIImpl(new byte[] {0, 3, 98, 39});

      byte[] dataTa = new byte[] {10, 20, 30, 40};
      SignalInfo signalInfo = new SignalInfoImpl(dataTa);
      ProtocolId protocolId = ProtocolId.gsm_0806;
      ExternalSignalInfo gsmBearerCapability = new ExternalSignalInfoImpl(signalInfo, protocolId, null);
      ExternalSignalInfo networkSignalInfo = new ExternalSignalInfoImpl(signalInfo, protocolId, null);

      boolean suppressionOfAnnouncement = false;
      ISDNAddressString gmscAddress = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "22226");
      boolean orInterrogation = false;
      boolean ccbsCall = false;
      boolean orNotSupportedInGMSC = false;
      boolean prePagingSupported = false;
      boolean longFTNSupported = false;
      boolean suppressVtCsi = false;
      boolean mtRoamingRetrySupported = false;
      ArrayList<LocationArea> locationAreas = new ArrayList<>();
      LACImpl lac = new LACImpl(123);
      LocationAreaImpl la = new LocationAreaImpl(lac);
      locationAreas.add(la);
      PagingArea pagingArea = new PagingAreaImpl(locationAreas);
      boolean mtrfIndicator = false;

      mapDialogMobility.addProvideRoamingNumberRequest(imsi, mscNumber, msisdn, lmsi,
          gsmBearerCapability, networkSignalInfo, suppressionOfAnnouncement, gmscAddress, null,
          orInterrogation, null, null, ccbsCall, null, null, orNotSupportedInGMSC,
          prePagingSupported, longFTNSupported, suppressVtCsi, null, mtRoamingRetrySupported, pagingArea,
          null, mtrfIndicator, null);

      mapDialogMobility.send();

    } catch (Exception e) {
      logger.error("Error while sending MAP Provide-Roaming-Number", e);
    }
  }

  public void insertSubscriberDataRequest(String imsiString, String domain) {
    try {
      logger.info("Sending MAP INSERT-SUBSCRIBER-DATA Request");
      MAPProvider clientMapProvider = this.mapClient.getMAPProvider();
      // preparing congestion control -- code not changing
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control. Simulator test for MAP ISD: networkIdState={}", networkIdState);
        Thread.sleep(3000);
      }

      this.rateLimiterObj.acquire();
      clientMapProvider.getMAPServiceMobility().activate();

      MAPApplicationContext appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.subscriberDataMngtContext, MAPApplicationContextVersion.version3);

      MAPDialogMobility clientDialogMobility = clientMapProvider.getMAPServiceMobility().createNewDialog(appCnt,
              address("hplmnHlr"), null,
              address("toVplmn"), null);

      IMSI imsi = this.mapParameterFactory.createIMSI(imsiString);
      // msisdn
      ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "882285105682451");
      // category
      Category category = new CategoryImpl(CategoryValue.ordinaryCallingSubscriber);
      // subscriberStatus
      SubscriberStatus subscriberStatus = SubscriberStatus.serviceGranted;
      if (domain.equalsIgnoreCase("ps")) {
        // bearerServiceList
        ArrayList<ExtBearerServiceCode> bearerServiceList = new ArrayList<>();
        ExtBearerServiceCode extBearerServiceCode1 = new ExtBearerServiceCodeImpl(BearerServiceCodeValue.allDataCDAServices);
        ExtBearerServiceCode extBearerServiceCode2 = new ExtBearerServiceCodeImpl(BearerServiceCodeValue.allDataCDS_Services);
        bearerServiceList.add(extBearerServiceCode1);
        bearerServiceList.add(extBearerServiceCode2);
        // teleserviceList
        ArrayList<ExtTeleserviceCode> teleserviceList = getExtTeleserviceCodes();
        // provisionedSS
        ArrayList<ExtSSInfo> provisionedSS = new ArrayList<>();
        SSCode clip = new SSCodeImpl(SupplementaryCodeValue.clip);
        ExtSSStatus clipExtSSStatus = new ExtSSStatusImpl(true, true, false, true);
        SSSubscriptionOption clipSubscriptionOption = new SSSubscriptionOptionImpl(OverrideCategory.overrideDisabled);
        ArrayList<ExtBasicServiceCode> basicServiceGroupList = null;
        ExtSSData extSSDataClip = new ExtSSDataImpl(clip, clipExtSSStatus, clipSubscriptionOption, basicServiceGroupList, null);
        SSCode clir = new SSCodeImpl(SupplementaryCodeValue.clir);
        ExtSSStatus clirExtSSStatus = new ExtSSStatusImpl(false, true, false, true);
        SSSubscriptionOption clirSubscriptionOption = new SSSubscriptionOptionImpl(CliRestrictionOption.temporaryDefaultAllowed);
        ExtSSData extSSDataClir = new ExtSSDataImpl(clir, clirExtSSStatus, clirSubscriptionOption, basicServiceGroupList, null);
        ExtSSInfo ssInfoClip = new ExtSSInfoImpl(extSSDataClip);
        ExtSSInfo ssInfoClir = new ExtSSInfoImpl(extSSDataClir);
        provisionedSS.add(ssInfoClip);
        provisionedSS.add(ssInfoClir);
        // roamingRestrictionDueToUnsupportedFeature
        boolean roamingRestrictionDueToUnsupportedFeature = true;
        // regionalSubscriptionData
        ArrayList<ZoneCode> regionalSubscriptionData = new ArrayList<>();
        ZoneCode zc1 = new ZoneCodeImpl(1);
        ZoneCode zc2 = new ZoneCodeImpl(2);
        ZoneCode zc3 = new ZoneCodeImpl(3);
        regionalSubscriptionData.add(zc1);
        regionalSubscriptionData.add(zc2);
        regionalSubscriptionData.add(zc3);
        // vbsSubscriptionData
        ArrayList<VoiceBroadcastData> vbsSubscriptionData = new ArrayList<>();
        GroupId gId = new GroupIdImpl("1");
        boolean broadcastInitEntitlement = true;
        LongGroupId lGId = new LongGroupIdImpl("5");
        VoiceBroadcastData voiceBroadcastData = new VoiceBroadcastDataImpl(gId, broadcastInitEntitlement, null, lGId);
        vbsSubscriptionData.add(voiceBroadcastData);
        // gprsSubscriptionData
        GPRSSubscriptionData gprsSubscriptionData = getGPRSSubscriptionData();
        // roamingRestrictedInSgsnDueToUnsupportedFeature
        boolean roamingRestrictedInSgsnDueToUnsupportedFeature = true;
        // networkAccessMode
        NetworkAccessMode networkAccessMode = NetworkAccessMode.onlyPacket;
        // lsaInformation
        LSAOnlyAccessIndicator lsaOnlyAccessIndicator = LSAOnlyAccessIndicator.accessOutsideLSAsAllowed;
        LSAIdentity lsaIdentity = new LSAIdentityImpl(new byte[] { 12, 10, 1 });
        ArrayList<LSAData> lsaDataList = new ArrayList<>();
        LSAIdentificationPriorityValue lsaIdentificationPriorityValue = LSAIdentificationPriorityValue.Priority_2;
        boolean preferentialAccessAvailable = true;
        boolean activeModeSupportAvailable = true;
        LSAAttributes lsaAttributes = new LSAAttributesImpl(lsaIdentificationPriorityValue, preferentialAccessAvailable,
            activeModeSupportAvailable);
        boolean lsaActiveModeIndicator = true;
        LSAData lsaData = new LSADataImpl(lsaIdentity, lsaAttributes, lsaActiveModeIndicator, null);
        lsaDataList.add(lsaData);
        LSAInformation lsaInformation = new LSAInformationImpl(gprsSubscriptionData.getCompleteDataListIncluded(), lsaOnlyAccessIndicator,
            lsaDataList, null);
        // lmuIndicator
        boolean lmuIndicator = true;
        // lcsInformation (gmlcList)
        LCSInformation lcsInformation = getLcsInformation();
        // istAlertTimer
        Integer istAlertTimer = 15;
        // superChargerSupportedInHLR
        AgeIndicator superChargerSupportedInHLR = new AgeIndicatorImpl(new byte[] { 4, 1, 48 });
        // sgsnCamelSubscriptionInfo
        SGSNCAMELSubscriptionInfo sgsnCamelSubscriptionInfo = getSGSNCAMELSubscriptionInfo();
        // accessRestrictionData
        boolean utranNotAllowed = false;
        boolean geranNotAllowed = false;
        boolean ganNotAllowed = true;
        boolean eUtranNotAllowed = false;
        boolean iHspaEvolutionNotAllowed = false;
        boolean hoToNon3GppAccessNotAllowed = true;
        AccessRestrictionData accessRestrictionData =
            new AccessRestrictionDataImpl(utranNotAllowed, geranNotAllowed, ganNotAllowed, iHspaEvolutionNotAllowed, eUtranNotAllowed, hoToNon3GppAccessNotAllowed);
        // ueReachabilityRequestIndicator
        boolean ueReachabilityRequestIndicator = true;
        // sgsnNumber
        ISDNAddressString sgsnNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "491710490000");
        // subscribedPeriodicRAUTAUtimer
        Long subscribedPeriodicRAUTAUtimer = 360L;
        // vplmnLIPAAllowed
        boolean vplmnLIPAAllowed = true;
        // mdtUserConsent
        boolean mdtUserConsent = true;
        // psAndSMSOnlyServiceProvision
        boolean psAndSMSOnlyServiceProvision = true;
        // smsInSGSNAllowed
        boolean smsInSGSNAllowed = true;
        // csToPsSRVCCAllowedIndicator
        boolean csToPsSRVCCAllowedIndicator = true;
        // pcscfRestorationRequest
        boolean pcscfRestorationRequest = true;
        // userPlaneIntegrityProtectionIndicator
        boolean userPlaneIntegrityProtectionIndicator = true;
        // dlBufferingSuggestedPacketCount
        Long dlBufferingSuggestedPacketCount = 3L;
        boolean iabOperationAllowedIndicator = false;
        clientDialogMobility.addInsertSubscriberDataRequest(imsi, msisdn, category, subscriberStatus, bearerServiceList, teleserviceList,
            provisionedSS, null, roamingRestrictionDueToUnsupportedFeature, regionalSubscriptionData, vbsSubscriptionData,
            null, null, null, null, gprsSubscriptionData,
            roamingRestrictedInSgsnDueToUnsupportedFeature, networkAccessMode, lsaInformation, lmuIndicator, lcsInformation,
            istAlertTimer, superChargerSupportedInHLR, null, null, sgsnCamelSubscriptionInfo,
            null, accessRestrictionData, null, null, null,
            ueReachabilityRequestIndicator, sgsnNumber, null, subscribedPeriodicRAUTAUtimer, vplmnLIPAAllowed, mdtUserConsent,
            null, null, null, psAndSMSOnlyServiceProvision, smsInSGSNAllowed,
            csToPsSRVCCAllowedIndicator, pcscfRestorationRequest, null, null, null,
            userPlaneIntegrityProtectionIndicator, dlBufferingSuggestedPacketCount, null, null,
            null, iabOperationAllowedIndicator);
      } else {
        // teleserviceList
        ArrayList<ExtTeleserviceCode> teleserviceList = new ArrayList<>();
        ExtTeleserviceCode shortMessageMT_PP = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.shortMessageMT_PP);
        ExtTeleserviceCode shortMessageMO_PP = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.shortMessageMO_PP);
        teleserviceList.add(shortMessageMT_PP);
        teleserviceList.add(shortMessageMO_PP);
        // provisionedSS
        ArrayList<ExtSSInfo> provisionedSS = new ArrayList<>();
        SSCode clip = new SSCodeImpl(SupplementaryCodeValue.clip);
        ExtSSStatus clipExtSSStatus = new ExtSSStatusImpl(false, true, false, true);
        SSSubscriptionOption clipSubscriptionOption = new SSSubscriptionOptionImpl(OverrideCategory.overrideDisabled);
        ArrayList<ExtBasicServiceCode> basicServiceGroupList = null;
        ExtSSData extSSDataClip = new ExtSSDataImpl(clip, clipExtSSStatus, clipSubscriptionOption, basicServiceGroupList, null);
        SSCode clir = new SSCodeImpl(SupplementaryCodeValue.clir);
        ExtSSStatus clirExtSSStatus = new ExtSSStatusImpl(false, true, false, true);
        SSSubscriptionOption clirSubscriptionOption = new SSSubscriptionOptionImpl(CliRestrictionOption.temporaryDefaultAllowed);
        ExtSSData extSSDataClir = new ExtSSDataImpl(clir, clirExtSSStatus, clirSubscriptionOption, basicServiceGroupList, null);
        ExtSSInfo ssInfoClip = new ExtSSInfoImpl(extSSDataClip);
        ExtSSInfo ssInfoClir = new ExtSSInfoImpl(extSSDataClir);
        provisionedSS.add(ssInfoClip);
        provisionedSS.add(ssInfoClir);
        // odbData
        boolean allOGCallsBarred = false;
        boolean internationalOGCallsBarred = false;
        boolean internationalOGCallsNotToHPLMNCountryBarred = false;
        boolean premiumRateInformationOGCallsBarred = true;
        boolean premiumRateEntertainmentOGCallsBarred = true;
        boolean ssAccessBarred = true;
        boolean interzonalOGCallsBarred = false;
        boolean interzonalOGCallsNotToHPLMNCountryBarred = false;
        boolean interzonalOGCallsAndInternationalOGCallsNotToHPLMNCountryBarred = false;
        boolean allECTBarred = false;
        boolean chargeableECTBarred = false;
        boolean internationalECTBarred = false;
        boolean interzonalECTBarred = false;
        boolean doublyChargeableECTBarred = true;
        boolean multipleECTBarred = true;
        boolean allPacketOrientedServicesBarred = false;
        boolean roamerAccessToHPLMNAPBarred = false;
        boolean roamerAccessToVPLMNAPBarred = true;
        boolean roamingOutsidePLMNOGCallsBarred = false;
        boolean allICCallsBarred = false;
        boolean roamingOutsidePLMNICCallsBarred = true;
        boolean roamingOutsidePLMNICountryICCallsBarred = false;
        boolean roamingOutsidePLMNBarred = false;
        boolean roamingOutsidePLMNCountryBarred = false;
        boolean registrationAllCFBarred = true;
        boolean registrationCFNotToHPLMNBarred = true;
        boolean registrationInterzonalCFBarred = true;
        boolean registrationInterzonalCFNotToHPLMNBarred = true;
        boolean registrationInternationalCFBarred = true;
        ODBGeneralData oDBGeneralData = new ODBGeneralDataImpl(allOGCallsBarred, internationalOGCallsBarred, internationalOGCallsNotToHPLMNCountryBarred,
            premiumRateInformationOGCallsBarred, premiumRateEntertainmentOGCallsBarred, ssAccessBarred,
            interzonalOGCallsBarred, interzonalOGCallsNotToHPLMNCountryBarred, interzonalOGCallsAndInternationalOGCallsNotToHPLMNCountryBarred,
            allECTBarred, chargeableECTBarred, internationalECTBarred, interzonalECTBarred,
            doublyChargeableECTBarred, multipleECTBarred, allPacketOrientedServicesBarred, roamerAccessToHPLMNAPBarred, roamerAccessToVPLMNAPBarred,
            roamingOutsidePLMNOGCallsBarred, allICCallsBarred, roamingOutsidePLMNICCallsBarred,
            roamingOutsidePLMNICountryICCallsBarred, roamingOutsidePLMNBarred, roamingOutsidePLMNCountryBarred,
            registrationAllCFBarred, registrationCFNotToHPLMNBarred, registrationInterzonalCFBarred,
            registrationInterzonalCFNotToHPLMNBarred, registrationInternationalCFBarred);
        boolean plmnSpecificBarringType1 = true;
        boolean plmnSpecificBarringType2 = false;
        boolean plmnSpecificBarringType3 = false;
        boolean plmnSpecificBarringType4 = false;
        ODBHPLMNData odbHplmnData = new ODBHPLMNDataImpl(plmnSpecificBarringType1, plmnSpecificBarringType2, plmnSpecificBarringType3, plmnSpecificBarringType4);
        ODBData odbData = new ODBDataImpl(oDBGeneralData, odbHplmnData, null);
        // roamingRestrictionDueToUnsupportedFeature
        boolean roamingRestrictionDueToUnsupportedFeature = true;
        // vbsSubscriptionData
        ArrayList<VoiceBroadcastData> vbsSubscriptionData = new ArrayList<>();
        GroupId gId = new GroupIdImpl("1");
        boolean broadcastInitEntitlement = true;
        LongGroupId lGId = new LongGroupIdImpl("5");
        VoiceBroadcastData voiceBroadcastData = new VoiceBroadcastDataImpl(gId, broadcastInitEntitlement, null, lGId);
        vbsSubscriptionData.add(voiceBroadcastData);
        // vgcsSubscriptionData
        ArrayList<VoiceGroupCallData> vgcsSubscriptionData = new ArrayList<>();
        boolean privilegedUplinkRequest = true;
        boolean emergencyUplinkRequest = true;
        boolean emergencyReset = true;
        AdditionalSubscriptions addSubscriptions = new AdditionalSubscriptionsImpl(privilegedUplinkRequest, emergencyUplinkRequest, emergencyReset);
        BitSetStrictLength addInfoBitset = new BitSetStrictLength(136);
        addInfoBitset.set(0);
        addInfoBitset.set(1);
        addInfoBitset.set(24);
        AdditionalInfo addInfo = new AdditionalInfoImpl(addInfoBitset);
        VoiceGroupCallData voiceGroupCallData = new VoiceGroupCallDataImpl(gId, null, addSubscriptions, addInfo, lGId);
        vgcsSubscriptionData.add(voiceGroupCallData);
        // vlrCamelSubscriptionInfo
        OBcsmTriggerDetectionPoint oBcsmTDP = OBcsmTriggerDetectionPoint.routeSelectFailure;
        long serviceKey = 7L;
        ISDNAddressString gsmSCFAddress = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "491710460029");
        DefaultCallHandling defaultCallHandling = DefaultCallHandling.continueCall;
        OBcsmCamelTDPData oBcsmCamelTDPData = new OBcsmCamelTDPDataImpl(oBcsmTDP, serviceKey, gsmSCFAddress, defaultCallHandling, null);
        ArrayList<OBcsmCamelTDPData> oBcsmCamelTDPDataList = new ArrayList<>();
        oBcsmCamelTDPDataList.add(oBcsmCamelTDPData);
        Integer camelCapabilityHandling = 2;
        boolean notificationToCSE = true;
        boolean csiActive = true;
        OCSI oCSI = new OCSIImpl(oBcsmCamelTDPDataList, null, camelCapabilityHandling, notificationToCSE, csiActive);
        ArrayList<SSCode> ssEventList = new ArrayList<>();
        ssEventList.add(clip);
        ssEventList.add(clir);
        SSCamelData ssCamelData = new SSCamelDataImpl(ssEventList, gsmSCFAddress, null);
        SSCSI ssCsi = new SSCSIImpl(ssCamelData, null, notificationToCSE, csiActive);
        ArrayList<OBcsmCamelTdpCriteria> oBcsmCamelTDPCriteriaList = getOBcsmCamelTdpCriteria(oBcsmTDP, basicServiceGroupList);
        boolean tifCsi = true;
        ArrayList<MMCode> mobilityTriggers = new ArrayList<>();
        MMCode mmCode1 = new MMCodeImpl(MMCodeValue.IMSIAttach);
        MMCode mmCode2 = new MMCodeImpl(MMCodeValue.LocationUpdateInSameVLR);
        mobilityTriggers.add(mmCode1);
        mobilityTriggers.add(mmCode2);
        MCSI mcsi = new MCSIImpl(mobilityTriggers, serviceKey, gsmSCFAddress, null, notificationToCSE, csiActive);
        ArrayList<SMSCAMELTDPData> smsCamelTdpDataList = new ArrayList<>();
        SMSTriggerDetectionPoint smsTDP = SMSTriggerDetectionPoint.smsDeliveryRequest;
        DefaultSMSHandling defaultSMSHandling = DefaultSMSHandling.continueTransaction;
        SMSCAMELTDPData smscameltdpData = new SMSCAMELTDPDataImpl(smsTDP, serviceKey, gsmSCFAddress, defaultSMSHandling, null);
        smsCamelTdpDataList.add(smscameltdpData);
        SMSCSI smsCsi = new SMSCSIImpl(smsCamelTdpDataList, camelCapabilityHandling, null, notificationToCSE, csiActive);
        ArrayList<TBcsmCamelTDPData> tBcsmCamelTDPDataList = new ArrayList<>();
        TBcsmTriggerDetectionPoint tBcsmTDP1 = TBcsmTriggerDetectionPoint.tNoAnswer;
        TBcsmTriggerDetectionPoint tBcsmTDP2 = TBcsmTriggerDetectionPoint.tBusy;
        TBcsmCamelTDPData tBcsmCamelTDPData1 = new TBcsmCamelTDPDataImpl(tBcsmTDP1, serviceKey, gsmSCFAddress, defaultCallHandling, null);
        TBcsmCamelTDPData tBcsmCamelTDPData2 = new TBcsmCamelTDPDataImpl(tBcsmTDP2, serviceKey, gsmSCFAddress, defaultCallHandling, null);
        tBcsmCamelTDPDataList.add(tBcsmCamelTDPData1);
        tBcsmCamelTDPDataList.add(tBcsmCamelTDPData2);
        TCSI vtCsi = new TCSIImpl(tBcsmCamelTDPDataList, null, camelCapabilityHandling, notificationToCSE, csiActive);
        TBcsmTriggerDetectionPoint tBcsmTriggerDetectionPoint = TBcsmTriggerDetectionPoint.tNoAnswer;
        ArrayList<CauseValue> tCauseValueCriteria = new ArrayList<>();
        CauseValue tcv1 = new CauseValueImpl(CauseValueCodeValue.CallRejected);
        CauseValue tcv2 = new CauseValueImpl(CauseValueCodeValue.BearerCapabilityNotAuthorized);
        tCauseValueCriteria.add(tcv1);
        tCauseValueCriteria.add(tcv2);
        TBcsmCamelTdpCriteria tBcsmCamelTdpCriteria = new TBcsmCamelTdpCriteriaImpl(tBcsmTriggerDetectionPoint, basicServiceGroupList, tCauseValueCriteria);
        ArrayList<TBcsmCamelTdpCriteria> tBcsmCamelTdpCriteriaList = new ArrayList<>();
        tBcsmCamelTdpCriteriaList.add(tBcsmCamelTdpCriteria);
        ArrayList<DPAnalysedInfoCriterium> dpAnalysedInfoCriteriaList = new ArrayList<>();
        ISDNAddressString dialledNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "491714780432");
        DPAnalysedInfoCriterium dpAnalysedInfoCriterium = new DPAnalysedInfoCriteriumImpl(dialledNumber, serviceKey, gsmSCFAddress, defaultCallHandling, null);
        dpAnalysedInfoCriteriaList.add(dpAnalysedInfoCriterium);
        DCSI dCSI = new DCSIImpl(dpAnalysedInfoCriteriaList, camelCapabilityHandling, null, notificationToCSE, csiActive);
        SMSCSI mtSmsCSI = new SMSCSIImpl(smsCamelTdpDataList, camelCapabilityHandling, null, notificationToCSE, csiActive);
        ArrayList<MTsmsCAMELTDPCriteria> mtSmsCamelTdpCriteriaList = new ArrayList<>();
        ArrayList<MTSMSTPDUType> mtsmstpduTypeArrayList = new ArrayList<>();
        MTSMSTPDUType mtsmstpduType1 = MTSMSTPDUType.smsDELIVER;
        MTSMSTPDUType mtsmstpduType2 = MTSMSTPDUType.smsSUBMITREPORT;
        MTSMSTPDUType mtsmstpduType3 = MTSMSTPDUType.smsSTATUSREPORT;
        mtsmstpduTypeArrayList.add(mtsmstpduType1);
        mtsmstpduTypeArrayList.add(mtsmstpduType2);
        mtsmstpduTypeArrayList.add(mtsmstpduType3);
        MTsmsCAMELTDPCriteria mTsmsCAMELTDPCriteria = new MTsmsCAMELTDPCriteriaImpl(smsTDP, mtsmstpduTypeArrayList);
        mtSmsCamelTdpCriteriaList.add(mTsmsCAMELTDPCriteria);
        VlrCamelSubscriptionInfo vlrCamelSubscriptionInfo = new VlrCamelSubscriptionInfoImpl(oCSI, null,
            ssCsi, oBcsmCamelTDPCriteriaList, tifCsi, mcsi, smsCsi, vtCsi, tBcsmCamelTdpCriteriaList, dCSI, mtSmsCSI,
            mtSmsCamelTdpCriteriaList);
        // naeaPreferredCI
        String carrierCode = "458";
        NetworkIdentificationPlanValue networkIdentificationPlanValue = NetworkIdentificationPlanValue.spare_1;
        NetworkIdentificationTypeValue networkIdentificationTypeValue = NetworkIdentificationTypeValue.nationalNetworkIdentification;
        NAEACIC naeaPreferredCIC = new NAEACICImpl(carrierCode, networkIdentificationPlanValue, networkIdentificationTypeValue);
        NAEAPreferredCI naeaPreferredCI = new NAEAPreferredCIImpl(naeaPreferredCIC, null);
        // roamingRestrictedInSgsnDueToUnsupportedFeature
        boolean roamingRestrictedInSgsnDueToUnsupportedFeature = false;
        // networkAccessMode
        NetworkAccessMode networkAccessMode = NetworkAccessMode.packetAndCircuit;
        // lmuIndicator
        boolean lmuIndicator = false;
        // lcsInformation
        LCSInformation lcsInformation = getLcsInformation();
        // istAlertTimer
        Integer istAlertTimer = 200;
        // mcSsInfo
        SSCode ssCode = new SSCodeImpl(SupplementaryCodeValue.cfu);
        ExtSSStatus ssStatus = new ExtSSStatusImpl(true, false, true, false);
        int nbrSB = 2;
        int nbrUser = 4;
        MCSSInfo mcSsInfo = new MCSSInfoImpl(ssCode, ssStatus, nbrSB, nbrUser, null);
        // csAllocationRetentionPriority
        CSAllocationRetentionPriority csAllocationRetentionPriority = new CSAllocationRetentionPriorityImpl(4);
        // chargingCharacteristics
        boolean isNormalCharging = false;
        boolean isPrepaidCharging = false;
        boolean isFlatRateCharging = true;
        boolean isChargingByHotBillingCharging = false;
        ChargingCharacteristics chargingCharacteristics = new ChargingCharacteristicsImpl(isNormalCharging, isPrepaidCharging, isFlatRateCharging, isChargingByHotBillingCharging);
        // accessRestrictionData
        boolean utranNotAllowed = false;
        boolean geranNotAllowed = false;
        boolean ganNotAllowed = true;
        boolean eUtranNotAllowed = false;
        boolean iHspaEvolutionNotAllowed = false;
        boolean hoToNon3GppAccessNotAllowed = true;
        AccessRestrictionData accessRestrictionData = new AccessRestrictionDataImpl(utranNotAllowed, geranNotAllowed, ganNotAllowed, iHspaEvolutionNotAllowed, eUtranNotAllowed, hoToNon3GppAccessNotAllowed);
        // epsSubscriptionData
        int defaultContext = 1;
        boolean completeDataListIncluded = true;
        PDNType pDNType = new PDNTypeImpl(PDNTypeValue.IPv4v6);
        PDPAddress servedPartyIPIPv4Address = null;
        APN apn = new APNImpl("internet");
        QoSClassIdentifier qci = QoSClassIdentifier.QCI_5;
        int priorityLevel = 9;
        Boolean preEmptionCapability = true;
        Boolean preEmptionVulnerability = false;
        AllocationRetentionPriority arp = new AllocationRetentionPriorityImpl(priorityLevel, preEmptionCapability, preEmptionVulnerability, null);
        EPSQoSSubscribed ePSQoSSubscribed = new EPSQoSSubscribedImpl(qci, arp, null);
        PDNGWIdentity pdnGwIdentity = null;
        PDNGWAllocationType pdnGwAllocationType = null;
        boolean vplmnAddressAllowed = true;
        int maxRequestedBandwidthUL = 2048;
        int maxRequestedBandwidthDL = 4096;
        AMBR ambr = new AMBRImpl(maxRequestedBandwidthUL, maxRequestedBandwidthDL, null);
        ArrayList<SpecificAPNInfo> specificAPNInfoList = null;
        APNOIReplacement apnOiReplacement = new APNOIReplacementImpl(new byte[] { 81, 92, 83, 84, 85, 86, 87, 88, 89 });
        SIPTOPermission sipToPermission = SIPTOPermission.siptoAllowed;
        LIPAPermission lipaPermission = LIPAPermission.lipaConditional;
        int contextId = 1;
        PDPAddress servedPartyIPIPv6Address = new PDPAddressImpl(new byte[] { 21 });
        APNConfiguration apnConfiguration = new APNConfigurationImpl(contextId, pDNType, servedPartyIPIPv4Address, apn,
            ePSQoSSubscribed, pdnGwIdentity, pdnGwAllocationType, vplmnAddressAllowed, chargingCharacteristics, ambr,
            specificAPNInfoList, null, servedPartyIPIPv6Address, apnOiReplacement, sipToPermission, lipaPermission);
        ArrayList<APNConfiguration> ePSDataList = new ArrayList<>();
        ePSDataList.add(apnConfiguration);
        APNConfigurationProfile apnConfigurationProfile = new APNConfigurationProfileImpl(defaultContext, completeDataListIncluded,
            ePSDataList, null);
        Integer rfspId = 0;
        ISDNAddressString stnSr = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "491710490000");
        boolean mpsCSPriority = true;
        boolean mpsEPSPriority = true;
        EPSSubscriptionData epsSubscriptionData = new EPSSubscriptionDataImpl(apnOiReplacement, rfspId, ambr, apnConfigurationProfile,
            stnSr, null, mpsCSPriority, mpsEPSPriority);
        // csgSubscriptionDataList
        BitSetStrictLength csgIdBitSet = new BitSetStrictLength(27);
        csgIdBitSet.set(0);
        csgIdBitSet.set(1);
        csgIdBitSet.set(25);
        csgIdBitSet.set(26);
        CSGId csgId = new CSGIdImpl(csgIdBitSet);
        int year = 2024;
        int month = 7;
        int day = 4;
        int hour = 19;
        int minute = 20;
        int second = 10;
        Time expirationDate = new TimeImpl(year, month, day, hour, minute, second);
        ArrayList<APN> lipaAllowedAPNList = new ArrayList<>();
        lipaAllowedAPNList.add(apn);
        CSGSubscriptionData csgSubscriptionData = new CSGSubscriptionDataImpl(csgId, expirationDate, null, lipaAllowedAPNList);
        ArrayList<CSGSubscriptionData> csgSubscriptionDataList = new ArrayList<>();
        csgSubscriptionDataList.add(csgSubscriptionData);
        // ueReachabilityRequestIndicator
        boolean ueReachabilityRequestIndicator = true;
        // sgsnNumber
        ISDNAddressString sgsnNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "491710490000");
        // mmeName
        DiameterIdentity mmeName = new DiameterIdentityImpl("mmec20.mmegi800.epc.mnc001.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8));
        // subscribedPeriodicRAUTAUtimer
        Long subscribedPeriodicRAUTAUtimer = 300L;
        // vplmnLIPAAllowed
        boolean vplmnLIPAAllowed = true;
        // mdtUserConsent
        boolean mdtUserConsent = false;
        // subscribedPeriodicLAUtimer
        Long subscribedPeriodicLAUtimer = 360L;
        // vplmnCSGSubscriptionDataList
        ArrayList<CSGSubscriptionData> vplmnCSGSubscriptionDataList = new ArrayList<>();
        vplmnCSGSubscriptionDataList.add(csgSubscriptionData);
        // additionalMSISDN
        ISDNAddressString additionalMSISDN = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "4917105682451");
        // psAndSMSOnlyServiceProvision
        boolean psAndSMSOnlyServiceProvision = true;
        // smsInSGSNAllowed
        boolean smsInSGSNAllowed = true;
        // csToPsSRVCCAllowedIndicator
        boolean csToPsSRVCCAllowedIndicator = true;
        // pcscfRestorationRequest
        boolean pcscfRestorationRequest = true;
        // adjacentAccessRestrictionDataList
        int mcc = 262;
        int mnc = 999;
        PlmnId plmnId = new PlmnIdImpl(mcc, mnc);
        boolean nrAsSecondaryRATNotAllowed = true;
        boolean unlicensedSpectrumAsSecondaryRATNotAllowed = false;
        ExtAccessRestrictionData extAccessRestrictionData =
            new ExtAccessRestrictionDataImpl(nrAsSecondaryRATNotAllowed, unlicensedSpectrumAsSecondaryRATNotAllowed); // extAccessRestrictionData
        AdjacentAccessRestrictionData adjacentAccessRestrictionData =
            new AdjacentAccessRestrictionDataImpl(plmnId, accessRestrictionData, extAccessRestrictionData);
        ArrayList<AdjacentAccessRestrictionData> adjacentAccessRestrictionDataList = new ArrayList<>();
        adjacentAccessRestrictionDataList.add(adjacentAccessRestrictionData);
        // imsiGroupIdList
        Long groupServiceId = 1L;
        LocalGroupId localGroupId = new LocalGroupIdImpl("120".getBytes(StandardCharsets.UTF_8));
        IMSIGroupId imsiGroupId = new IMSIGroupIdImpl(groupServiceId, plmnId, localGroupId);
        ArrayList<IMSIGroupId> imsiGroupIdList = new ArrayList<>();
        imsiGroupIdList.add(imsiGroupId);
        // ueUsageType
        UEUsageType ueUsageType = new UEUsageTypeImpl(new byte[] {0, 0, 0, (byte) 0x87});
        // userPlaneIntegrityProtectionIndicator
        boolean userPlaneIntegrityProtectionIndicator = true;
        // dlBufferingSuggestedPacketCount
        Long dlBufferingSuggestedPacketCount = 0L;
        // resetIdList
        ArrayList<ResetId> resetIdList = new ArrayList<>();
        ResetId resetId = new ResetIdImpl(new byte[] {1, 2, 4, (byte) 0x80});
        resetIdList.add(resetId);
        // eDRXCycleLengthList
        UsedRATType usedRATType = UsedRATType.nbIoT;
        byte[] edrCycleLengthVal = new byte[] { 0x02 };
        EDRXCycleLengthValue eDRXCycleLengthValue = new EDRXCycleLengthValueImpl(edrCycleLengthVal);
        EDRXCycleLength edrxCycleLength = new EDRXCycleLengthImpl(usedRATType, eDRXCycleLengthValue);
        ArrayList<EDRXCycleLength> eDRXCycleLengthList = new ArrayList<>();
        eDRXCycleLengthList.add(edrxCycleLength);
        // iabOperationAllowedIndicator
        boolean iabOperationAllowedIndicator = true;
        clientDialogMobility.addInsertSubscriberDataRequest(imsi, msisdn, category, subscriberStatus, null, teleserviceList,
            provisionedSS, odbData, roamingRestrictionDueToUnsupportedFeature, null, vbsSubscriptionData,
            vgcsSubscriptionData, vlrCamelSubscriptionInfo, null, naeaPreferredCI, null,
            roamingRestrictedInSgsnDueToUnsupportedFeature, networkAccessMode, null, lmuIndicator, lcsInformation,
            istAlertTimer, null, mcSsInfo, csAllocationRetentionPriority, null,
            chargingCharacteristics, accessRestrictionData, null, epsSubscriptionData, csgSubscriptionDataList,
            ueReachabilityRequestIndicator, sgsnNumber, mmeName, subscribedPeriodicRAUTAUtimer, vplmnLIPAAllowed, mdtUserConsent,
            subscribedPeriodicLAUtimer, vplmnCSGSubscriptionDataList, additionalMSISDN, psAndSMSOnlyServiceProvision, smsInSGSNAllowed,
            csToPsSRVCCAllowedIndicator, pcscfRestorationRequest, adjacentAccessRestrictionDataList, imsiGroupIdList, ueUsageType,
            userPlaneIntegrityProtectionIndicator, dlBufferingSuggestedPacketCount, resetIdList, eDRXCycleLengthList,
            extAccessRestrictionData, iabOperationAllowedIndicator);
      }

      clientDialogMobility.send();

    } catch (Exception e) {
      logger.error("Error while sending MAP Insert-Subscriber-Data", e);
    }
  }



  private static ReSynchronisationInfo getReSynchronisationInfo() {
    byte[] rand = new byte[] {(byte) 0xf6, (byte) 0xe2, (byte) 0xc3, (byte) 0xdc, (byte) 0xa4, (byte) 0xca,
        (byte) 0xae, (byte) 0x9e, 0x4c, (byte) 0xba, 0x0f, (byte) 0xd3, 0x42, 0x72, (byte) 0xee, 0x46};
    byte[] auts = new byte[] {(byte) 0xe9, 0x15, (byte) 0x97, (byte) 0x88, (byte) 0xbc, (byte) 0xeb, (byte) 0x80,
        0x00, (byte) 0x81, 0x3f, (byte) 0xc0, 0x40, (byte) 0xff, 0x53};
    return new ReSynchronisationInfoImpl(rand, auts);
  }

  private static NetworkNodeDiameterAddress getNetworkNodeDiameterAddress() {
    byte[] mmeNameBytes = {0x6d, 0x6d, 0x65, 0x2e, 0x32, 0x30, 0x2e, 0x6d, 0x61, 0x67, 0x2e, 0x65, 0x70, 0x63, 0x2e, 0x6d,
        0x6e, 0x63, 0x30, 0x30, 0x31, 0x2e, 0x6d, 0x63, 0x63, 0x37, 0x34, 0x38, 0x2e, 0x33, 0x67, 0x70,
        0x70, 0x6e, 0x65, 0x74, 0x77, 0x6f, 0x72, 0x6b, 0x2e, 0x6f, 0x72, 0x67};
    byte[] mmeRealmBytes = {0x65, 0x70, 0x63, 0x2e, 0x6d, 0x6e, 0x63, 0x30, 0x30, 0x31, 0x2e, 0x6d, 0x63, 0x63, 0x37, 0x34,
        0x38, 0x2e, 0x33, 0x67, 0x70, 0x70, 0x6e, 0x65, 0x74, 0x77, 0x6f, 0x72, 0x6b, 0x2e, 0x6f, 0x72, 0x67};
    DiameterIdentity mmeName = new DiameterIdentityImpl(mmeNameBytes);
    DiameterIdentity mmeRealm = new DiameterIdentityImpl(mmeRealmBytes);
    return new NetworkNodeDiameterAddressImpl(mmeName, mmeRealm);
  }

  private static VLRCapability getVlrCapability() {
    SupportedCamelPhases supportedCamelPhases = new SupportedCamelPhasesImpl(true, true, false, false);
    boolean solsaSupportIndicator = true;
    ISTSupportIndicator istSupportIndicator = ISTSupportIndicator.istCommandSupported;
    SuperChargerInfo superChargerSupportedInServingNetworkEntity = new SuperChargerInfoImpl(true);
    boolean longFtnSupported = true;
    SupportedLCSCapabilitySets supportedLCSCapabilitySets = new SupportedLCSCapabilitySetsImpl(true, true, true, true, false);
    boolean oCsi = false;
    boolean dCsi = false;
    boolean vtCsi = false;
    boolean tCsi = false;
    boolean mtSMSCsi = true;
    boolean mgCsi = true;
    boolean psiEnhancements = true;
    OfferedCamel4CSIs offeredCamel4CSIs = new OfferedCamel4CSIsImpl(oCsi,dCsi,vtCsi,tCsi, mtSMSCsi, mgCsi, psiEnhancements);
    boolean utran = true;
    boolean geran = true;
    boolean gan = false;
    boolean i_hspa_evolution = true;
    boolean e_utran = true;
    boolean nb_iot = true;
    SupportedRATTypes supportedRATTypesIndicator = new SupportedRATTypesImpl(utran, geran, gan, i_hspa_evolution, e_utran, nb_iot);
    boolean longGroupIDSupported = true;
    boolean mtRoamingForwardingSupported = true;
    return new VLRCapabilityImpl(supportedCamelPhases, null, solsaSupportIndicator,
        istSupportIndicator, superChargerSupportedInServingNetworkEntity, longFtnSupported, supportedLCSCapabilitySets,
        offeredCamel4CSIs, supportedRATTypesIndicator, longGroupIDSupported, mtRoamingForwardingSupported);
  }

  private static SupportedFeatures getSupportedFeatures() {
    boolean odbAllApn = false;
    boolean odbHPLMNApn = false;
    boolean odbVPLMNApn = false;
    boolean odbAllOg = false;
    boolean odbAllInternationalOg = false;
    boolean odbAllIntOgNotToHPLMNCountry = false;
    boolean odbAllInterzonalOg = false;
    boolean odbAllInterzonalOgNotToHPLMNCountry = false;
    boolean odbAllInterzonalOgandInternatOgNotToHPLMNCountry = false;
    boolean regSub = false;
    boolean trace = false;
    boolean lcsAllPrivExcep = true;
    boolean lcsUniversal = true;
    boolean lcsCallSessionRelated = true;
    boolean lcsCallSessionUnrelated = true;
    boolean lcsPLMNOperator = true;
    boolean lcsServiceType = true;
    boolean lcsAllMOLRSS = true;
    boolean lcsBasicSelfLocation = true;
    boolean lcsAutonomousSelfLocation = true;
    boolean lcsTransferToThirdParty = true;
    boolean smMoPp = true;
    boolean barringOutgoingCalls = true;
    boolean baoc = false;
    boolean boic = false;
    boolean boicExHC = false;
    boolean localTimeZoneRetrieval = true;
    boolean additionalMsisdn = true;
    boolean smsInMME = true;
    boolean smsInSGSN = true;
    boolean ueReachabilityNotification = true;
    boolean stateLocationInformationRetrieval = true;
    boolean partialPurge = true;
    boolean gddInSGSN = true;
    boolean sgsnCAMELCapability = true;
    boolean pcscfRestoration = true;
    boolean dedicatedCoreNetworks = true;
    boolean nonIPPDNTypeAPNs = true;
    boolean nonIPPDPTypeAPNs = true;
    boolean nrAsSecondaryRAT = true;
    return new SupportedFeaturesImpl(odbAllApn, odbHPLMNApn, odbVPLMNApn, odbAllOg, odbAllInternationalOg,
        odbAllIntOgNotToHPLMNCountry, odbAllInterzonalOg, odbAllInterzonalOgNotToHPLMNCountry,
        odbAllInterzonalOgandInternatOgNotToHPLMNCountry, regSub, trace, lcsAllPrivExcep, lcsUniversal,
        lcsCallSessionRelated, lcsCallSessionUnrelated, lcsPLMNOperator, lcsServiceType, lcsAllMOLRSS,
        lcsBasicSelfLocation, lcsAutonomousSelfLocation, lcsTransferToThirdParty, smMoPp, barringOutgoingCalls, baoc,
        boic, boicExHC, localTimeZoneRetrieval, additionalMsisdn, smsInMME, smsInSGSN, ueReachabilityNotification,
        stateLocationInformationRetrieval, partialPurge, gddInSGSN, sgsnCAMELCapability,
        pcscfRestoration, dedicatedCoreNetworks, nonIPPDNTypeAPNs, nonIPPDPTypeAPNs,
        nrAsSecondaryRAT);
  }

  private static ArrayList<OBcsmCamelTdpCriteria> getOBcsmCamelTdpCriteria(OBcsmTriggerDetectionPoint oBcsmTDP, ArrayList<ExtBasicServiceCode> basicServiceGroupList) {
    DestinationNumberCriteria destinationNumberCriteria = getDestinationNumberCriteria();
    CallTypeCriteria callTypeCriteria = CallTypeCriteria.notForwarded;
    ArrayList<CauseValue> oCauseValueCriteria = new ArrayList<>();
    CauseValue causeValue1 = new CauseValueImpl(CauseValueCodeValue.InvalidCallReferenceValue);
    CauseValue causeValue2 = new CauseValueImpl(CauseValueCodeValue.BearerCapabilityNotAuthorized);
    oCauseValueCriteria.add(causeValue1);
    oCauseValueCriteria.add(causeValue2);
    ArrayList<OBcsmCamelTdpCriteria> oBcsmCamelTDPCriteriaList = new ArrayList<>();
    OBcsmCamelTdpCriteria oBcsmCamelTdpCriteria = new OBcsmCamelTdpCriteriaImpl(oBcsmTDP, destinationNumberCriteria,
        basicServiceGroupList, callTypeCriteria, oCauseValueCriteria, null);
    Result result = new Result(oBcsmCamelTDPCriteriaList, oBcsmCamelTdpCriteria);
    result.oBcsmCamelTDPCriteriaList.add(result.oBcsmCamelTdpCriteria);
    return result.oBcsmCamelTDPCriteriaList;
  }

  private static class Result {
    public final ArrayList<OBcsmCamelTdpCriteria> oBcsmCamelTDPCriteriaList;
    public final OBcsmCamelTdpCriteria oBcsmCamelTdpCriteria;

    public Result(ArrayList<OBcsmCamelTdpCriteria> oBcsmCamelTDPCriteriaList, OBcsmCamelTdpCriteria oBcsmCamelTdpCriteria) {
      this.oBcsmCamelTDPCriteriaList = oBcsmCamelTDPCriteriaList;
      this.oBcsmCamelTdpCriteria = oBcsmCamelTdpCriteria;
    }
  }

  private static DestinationNumberCriteria getDestinationNumberCriteria() {
    MatchType matchType = MatchType.enabling;
    ArrayList<ISDNAddressString> destinationNumberList = new ArrayList<>();
    ISDNAddressString destinationNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "491714780432");
    destinationNumberList.add(destinationNumber);
    ArrayList<Integer> destinationNumberLengthList = new ArrayList<>();
    destinationNumberLengthList.add(1);
    return new DestinationNumberCriteriaImpl(matchType, destinationNumberList, destinationNumberLengthList);
  }

  private static LCSInformation getLcsInformation() {
    ArrayList<ISDNAddressString> gmlcList = new ArrayList<>();
    ISDNAddressString gmlcAddress =
        new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "4917104600321");
    gmlcList.add(gmlcAddress);
    // lcsInformation (lcsPrivacyExceptionList)
    SSCode ssCode1 = new SSCodeImpl(SupplementaryCodeValue.allMOLR_SS);
    SSCode ssCode2 = new SSCodeImpl(SupplementaryCodeValue.autonomousSelfLocation);
    SSCode ssCode3 = new SSCodeImpl(SupplementaryCodeValue.allLCSPrivacyException);
    SSCode ssCode4 = new SSCodeImpl(SupplementaryCodeValue.allPLMN_specificSS);
    ExtSSStatus extSSStatus1 = new ExtSSStatusImpl(true, false, false, false);
    ExtSSStatus extSSStatus2 = new ExtSSStatusImpl(false, true, false, false);
    ExtSSStatus extSSStatus3 = new ExtSSStatusImpl(false, false, true, false);
    ExtSSStatus extSSStatus4 = new ExtSSStatusImpl(false, false, false, true);
    LCSClientInternalID lcsClientInternalID1 = LCSClientInternalID.broadcastService;
    LCSClientInternalID lcsClientInternalID2 = LCSClientInternalID.oandMHPLMN;
    LCSClientInternalID lcsClientInternalID3 = LCSClientInternalID.targetMSsubscribedService;
    LCSClientInternalID lcsClientInternalID4 = LCSClientInternalID.anonymousLocation;
    ArrayList<LCSClientInternalID> plmnClientList1 = new ArrayList<>();
    plmnClientList1.add(lcsClientInternalID1);
    plmnClientList1.add(lcsClientInternalID2);
    ArrayList<LCSClientInternalID> plmnClientList2 = new ArrayList<>();
    plmnClientList2.add(lcsClientInternalID2);
    ArrayList<LCSClientInternalID> plmnClientList3 = new ArrayList<>();
    plmnClientList3.add(lcsClientInternalID3);
    ArrayList<LCSClientInternalID> plmnClientList4 = new ArrayList<>();
    plmnClientList4.add(lcsClientInternalID4);
    ArrayList<ExternalClient> externalClientList1 = new ArrayList<>();
    ISDNAddressString externalAddress1 = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.reserved, "874927492");
    LCSClientExternalID clientIdentity1 = new LCSClientExternalIDImpl(externalAddress1, null);
    GMLCRestriction gmlcRestriction1 = GMLCRestriction.homeCountry;
    NotificationToMSUser notificationToMSUser1 = NotificationToMSUser.notifyLocationAllowed;
    ExternalClient externalClient1 = new ExternalClientImpl(clientIdentity1, gmlcRestriction1, notificationToMSUser1, null);
    externalClientList1.add(externalClient1);
    ArrayList<ExternalClient> externalClientList2 = new ArrayList<>();
    ISDNAddressString externalAddress2 = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.reserved, "398279222");
    LCSClientExternalID clientIdentity2 = new LCSClientExternalIDImpl(externalAddress2, null);
    GMLCRestriction gmlcRestriction2 = GMLCRestriction.gmlcList;
    NotificationToMSUser notificationToMSUser2 = NotificationToMSUser.notifyAndVerifyLocationAllowedIfNoResponse;
    ExternalClient externalClient2 = new ExternalClientImpl(clientIdentity2, gmlcRestriction2, notificationToMSUser2, null);
    externalClientList2.add(externalClient2);
    ArrayList<ExternalClient> externalClientList3 = new ArrayList<>();
    ISDNAddressString externalAddress3 = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.reserved, "23543252234");
    LCSClientExternalID clientIdentity3 = new LCSClientExternalIDImpl(externalAddress3, null);
    GMLCRestriction gmlcRestriction3 = GMLCRestriction.homeCountry;
    NotificationToMSUser notificationToMSUser3 = NotificationToMSUser.locationNotAllowed;
    ExternalClient externalClient3 = new ExternalClientImpl(clientIdentity3, gmlcRestriction3, notificationToMSUser3, null);
    externalClientList3.add(externalClient3);
    ArrayList<ExternalClient> externalClientList4 = new ArrayList<>();
    ISDNAddressString externalAddress4 = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.reserved, "598990298245");
    LCSClientExternalID clientIdentity4 = new LCSClientExternalIDImpl(externalAddress4, null);
    GMLCRestriction gmlcRestriction4 = GMLCRestriction.gmlcList;
    NotificationToMSUser notificationToMSUser4 = NotificationToMSUser.notifyAndVerifyLocationNotAllowedIfNoResponse;
    ExternalClient externalClient4 = new ExternalClientImpl(clientIdentity4, gmlcRestriction4, notificationToMSUser4, null);
    externalClientList4.add(externalClient4);
    ArrayList<ServiceType> serviceTypeList1 = new ArrayList<>();
    int serviceTypeIdentity1 = 1;
    ServiceType serviceType1 = new ServiceTypeImpl(serviceTypeIdentity1, gmlcRestriction1, notificationToMSUser1, null);
    serviceTypeList1.add(serviceType1);
    ArrayList<ServiceType> serviceTypeList2 = new ArrayList<>();
    int serviceTypeIdentity2 = 2;
    ServiceType serviceType2 = new ServiceTypeImpl(serviceTypeIdentity2, gmlcRestriction2, notificationToMSUser2, null);
    serviceTypeList2.add(serviceType2);
    ArrayList<ServiceType> serviceTypeList3 = new ArrayList<>();
    int serviceTypeIdentity3 = 3;
    ServiceType serviceType3 = new ServiceTypeImpl(serviceTypeIdentity3, gmlcRestriction3, notificationToMSUser3, null);
    serviceTypeList3.add(serviceType3);
    ArrayList<ServiceType> serviceTypeList4 = new ArrayList<>();
    int serviceTypeIdentity4 = 4;
    ServiceType serviceType4 = new ServiceTypeImpl(serviceTypeIdentity4, gmlcRestriction4, notificationToMSUser4, null);
    serviceTypeList4.add(serviceType4);
    LCSPrivacyClass lcsPrivacyClass1 = new LCSPrivacyClassImpl(ssCode1, extSSStatus1, notificationToMSUser1, externalClientList1,
        plmnClientList1, null, externalClientList1, serviceTypeList1);
    LCSPrivacyClass lcsPrivacyClass2 = new LCSPrivacyClassImpl(ssCode2, extSSStatus2, notificationToMSUser2, externalClientList2,
        plmnClientList2, null, externalClientList2, serviceTypeList2);
    LCSPrivacyClass lcsPrivacyClass3 = new LCSPrivacyClassImpl(ssCode3, extSSStatus3, notificationToMSUser3, externalClientList3,
        plmnClientList3, null, externalClientList3, serviceTypeList3);
    LCSPrivacyClass lcsPrivacyClass4 = new LCSPrivacyClassImpl(ssCode4, extSSStatus4, notificationToMSUser4, externalClientList4,
        plmnClientList4, null, externalClientList4, serviceTypeList4);
    ArrayList<LCSPrivacyClass> lcsPrivacyExceptionList = new ArrayList<>();
    lcsPrivacyExceptionList.add(lcsPrivacyClass1);
    lcsPrivacyExceptionList.add(lcsPrivacyClass2);
    lcsPrivacyExceptionList.add(lcsPrivacyClass3);
    lcsPrivacyExceptionList.add(lcsPrivacyClass4);
    // lcsInformation (molrList)
    ArrayList<MOLRClass> molrList = new ArrayList<>();
    MOLRClass molrClass1 = new MOLRClassImpl(ssCode1, extSSStatus1, null);
    MOLRClass molrClass2 = new MOLRClassImpl(ssCode2, extSSStatus2, null);
    MOLRClass molrClass3 = new MOLRClassImpl(ssCode3, extSSStatus3, null);
    molrList.add(molrClass1);
    molrList.add(molrClass2);
    molrList.add(molrClass3);
    // lcsInformation (addLcsPrivacyExceptionList)
    // add-lcs-PrivacyExceptionList may be sent only if lcs-PrivacyExceptionList is
    // present and contains four instances of LCS-PrivacyClass. If the mentioned condition
    // is not satisfied the receiving node shall discard add-lcs-PrivacyExceptionList.
    // If an LCS-PrivacyClass is received both in lcs-PrivacyExceptionList and in
    // add-lcs-PrivacyExceptionList with the same SS-Code, then the error unexpected
    // data value shall be returned.
    SSCode addSsCode = new SSCodeImpl(SupplementaryCodeValue.plmn_specificSS_1);
    ExtSSStatus addExtSSStatus = new ExtSSStatusImpl(true, true, false, true);
    ArrayList<LCSClientInternalID> addPlmnClientList = new ArrayList<>();
    addPlmnClientList.add(lcsClientInternalID1);
    addPlmnClientList.add(lcsClientInternalID2);
    addPlmnClientList.add(lcsClientInternalID3);
    addPlmnClientList.add(lcsClientInternalID4);
    ArrayList<ExternalClient> addExternalClientList = new ArrayList<>();
    ISDNAddressString addExternalAddress = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.reserved, "874927492");
    LCSClientExternalID addClientIdentity = new LCSClientExternalIDImpl(addExternalAddress, null);
    GMLCRestriction addGmlcRestriction = GMLCRestriction.homeCountry;
    NotificationToMSUser addMNtificationToMSUser = NotificationToMSUser.notifyLocationAllowed;
    ExternalClient addExternalClient = new ExternalClientImpl(addClientIdentity, addGmlcRestriction, addMNtificationToMSUser, null);
    addExternalClientList.add(addExternalClient);
    ArrayList<ServiceType> addServiceTypeList = new ArrayList<>();
    int addServiceTypeIdentity = 5;
    ServiceType addServiceType4 = new ServiceTypeImpl(addServiceTypeIdentity, addGmlcRestriction, addMNtificationToMSUser, null);
    addServiceTypeList.add(addServiceType4);
    LCSPrivacyClass addLcsPrivacyClass = new LCSPrivacyClassImpl(addSsCode, addExtSSStatus, addMNtificationToMSUser, addExternalClientList,
        addPlmnClientList, null, addExternalClientList, addServiceTypeList);
    ArrayList<LCSPrivacyClass> addLcsPrivacyExceptionList = new ArrayList<>();
    addLcsPrivacyExceptionList.add(addLcsPrivacyClass);
    return new LCSInformationImpl(gmlcList, lcsPrivacyExceptionList, molrList, addLcsPrivacyExceptionList);
  }

  private static SGSNCAMELSubscriptionInfo getSGSNCAMELSubscriptionInfo() {
    GPRSTriggerDetectionPoint gprsTriggerDetectionPoint = GPRSTriggerDetectionPoint.attach;
    long sk = 12;
    ISDNAddressString gsmSCFAddress = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "491710460029");
    DefaultGPRSHandling defaultSessionHandling = DefaultGPRSHandling.continueTransaction;
    GPRSCamelTDPData gprsCamelTDPData = new GPRSCamelTDPDataImpl(gprsTriggerDetectionPoint, sk, gsmSCFAddress, defaultSessionHandling, null);
    ArrayList<GPRSCamelTDPData> gprsCamelTDPDataList = new ArrayList<>();
    gprsCamelTDPDataList.add(gprsCamelTDPData);
    Integer camelCapabilityHandling = 3;
    boolean notificationToCSE = true;
    boolean csiActive = true;
    GPRSCSI gprsCsi = new GPRSCSIImpl(gprsCamelTDPDataList, camelCapabilityHandling, null, notificationToCSE, csiActive);
    int serviceKey = 7;
    ArrayList<SMSCAMELTDPData> smsCamelTdpDataList = new ArrayList<>();
    SMSTriggerDetectionPoint smsTDP = SMSTriggerDetectionPoint.smsDeliveryRequest;
    DefaultSMSHandling defaultSMSHandling = DefaultSMSHandling.continueTransaction;
    SMSCAMELTDPData smscameltdpData = new SMSCAMELTDPDataImpl(smsTDP, serviceKey, gsmSCFAddress, defaultSMSHandling, null);
    smsCamelTdpDataList.add(smscameltdpData);
    SMSCSI moSmsCsi = new SMSCSIImpl(smsCamelTdpDataList, camelCapabilityHandling, null, notificationToCSE, csiActive);
    SMSCSI mtSmsCSI = new SMSCSIImpl(smsCamelTdpDataList, camelCapabilityHandling, null, notificationToCSE, csiActive);
    ArrayList<MTsmsCAMELTDPCriteria> mtSmsCamelTdpCriteriaList = new ArrayList<>();
    ArrayList<MTSMSTPDUType> mtsmstpduTypeArrayList = new ArrayList<>();
    MTSMSTPDUType mtsmstpduType1 = MTSMSTPDUType.smsDELIVER;
    MTSMSTPDUType mtsmstpduType2 = MTSMSTPDUType.smsSUBMITREPORT;
    MTSMSTPDUType mtsmstpduType3 = MTSMSTPDUType.smsSTATUSREPORT;
    mtsmstpduTypeArrayList.add(mtsmstpduType1);
    mtsmstpduTypeArrayList.add(mtsmstpduType2);
    mtsmstpduTypeArrayList.add(mtsmstpduType3);
    MTsmsCAMELTDPCriteria mTsmsCAMELTDPCriteria = new MTsmsCAMELTDPCriteriaImpl(smsTDP, mtsmstpduTypeArrayList);
    mtSmsCamelTdpCriteriaList.add(mTsmsCAMELTDPCriteria);
    ArrayList<MMCode> mobilityTriggers = new ArrayList<>();
    MMCode mmCode = new MMCodeImpl(MMCodeValue.GPRSAttach);
    mobilityTriggers.add(mmCode);
    MGCSI mgCsi = new MGCSIImpl(mobilityTriggers, serviceKey, gsmSCFAddress, null, notificationToCSE, csiActive);
    return new SGSNCAMELSubscriptionInfoImpl(gprsCsi, moSmsCsi,
        null, mtSmsCSI, mtSmsCamelTdpCriteriaList, mgCsi);
  }

  private static ArrayList<ExtTeleserviceCode> getExtTeleserviceCodes() {
    ArrayList<ExtTeleserviceCode> teleserviceList = new ArrayList<>();
    ExtTeleserviceCode dataTeleservices = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.allDataTeleservices);
    ExtTeleserviceCode shortMessageMT_PP = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.shortMessageMT_PP);
    ExtTeleserviceCode shortMessageMO_PP = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.shortMessageMO_PP);
    teleserviceList.add(dataTeleservices);
    teleserviceList.add(shortMessageMT_PP);
    teleserviceList.add(shortMessageMO_PP);
    return teleserviceList;
  }

  private static GPRSSubscriptionData getGPRSSubscriptionData() {
    boolean completeDataListIncluded = true;
    ArrayList<PDPContext> gprsDataList = new ArrayList<>();
    int pdpContextId = 1;
    PDPType pdpType = new PDPTypeImpl(PDPTypeValue.IPv4);
    PDPAddress pdpAddress = new PDPAddressImpl(new byte[] { 21 });
    QoSSubscribed_ReliabilityClass reliabilityClass = QoSSubscribed_ReliabilityClass.reserved_7;
    QoSSubscribed_DelayClass delayClass = QoSSubscribed_DelayClass.delay_Class_4_bestEffort;
    QoSSubscribed_PrecedenceClass precedenceClass = QoSSubscribed_PrecedenceClass.normalPriority;
    QoSSubscribed_PeakThroughput peakThroughput = QoSSubscribed_PeakThroughput.upTo_4000_octetS;
    QoSSubscribed_MeanThroughput meanThroughput = QoSSubscribed_MeanThroughput._2000_octetH;
    QoSSubscribed qosSubscribed = new QoSSubscribedImpl(reliabilityClass, delayClass, precedenceClass, peakThroughput, meanThroughput);
    boolean vplmnAddressAllowed = true;
    APN apn = null;
    try {
      apn = new APNImpl("internet");
    } catch (Exception e) {
      logger.error("Error building the GPRS subscription data", e);
    }
    int allocationRetentionPriority = 9;
    ExtQoSSubscribed_DeliveryOfErroneousSdus deliveryOfErroneousSdus = ExtQoSSubscribed_DeliveryOfErroneousSdus.erroneousSdusAreDelivered_Yes;
    ExtQoSSubscribed_DeliveryOrder deliveryOrder = ExtQoSSubscribed_DeliveryOrder.withoutDeliveryOrderNo;
    ExtQoSSubscribed_TrafficClass trafficClass = ExtQoSSubscribed_TrafficClass.interactiveClass;
    int maximumSduSizeData = 151;
    boolean isSourceData = true;
    ExtQoSSubscribed_MaximumSduSize maximumSduSize = new ExtQoSSubscribed_MaximumSduSizeImpl(maximumSduSizeData, isSourceData);
    int maximumBitRateForUL = 128;
    ExtQoSSubscribed_BitRate maximumBitRateForUplink = new ExtQoSSubscribed_BitRateImpl(maximumBitRateForUL, isSourceData);
    int maximumBitRateForDL = 576;
    ExtQoSSubscribed_BitRate maximumBitRateForDownlink = new ExtQoSSubscribed_BitRateImpl(maximumBitRateForDL, isSourceData);
    ExtQoSSubscribed_ResidualBER residualBER = ExtQoSSubscribed_ResidualBER.subscribedResidualBER_Reserved;
    ExtQoSSubscribed_SduErrorRatio sduErrorRatio = ExtQoSSubscribed_SduErrorRatio.subscribedSduErrorRatio_Reserved;
    ExtQoSSubscribed_TrafficHandlingPriority trafficHandlingPriority = ExtQoSSubscribed_TrafficHandlingPriority.priorityLevel_3;
    int transferDelayValue = 1000;
    ExtQoSSubscribed_TransferDelay transferDelay = new ExtQoSSubscribed_TransferDelayImpl(transferDelayValue, isSourceData);
    int gbrUL = 64;
    ExtQoSSubscribed_BitRate guaranteedBitRateForUplink = new ExtQoSSubscribed_BitRateImpl(gbrUL, isSourceData);
    int gbrDL = 256;
    ExtQoSSubscribed_BitRate guaranteedBitRateForDownlink = new ExtQoSSubscribed_BitRateImpl(gbrDL, isSourceData);
    ExtQoSSubscribed extQoSSubscribed = new ExtQoSSubscribedImpl(allocationRetentionPriority, deliveryOfErroneousSdus,
        deliveryOrder, trafficClass, maximumSduSize, maximumBitRateForUplink, maximumBitRateForDownlink, residualBER,
        sduErrorRatio, trafficHandlingPriority, transferDelay, guaranteedBitRateForUplink, guaranteedBitRateForDownlink);
    boolean isNormalCharging = true;
    boolean isPrepaidCharging = false;
    boolean isFlatRateCharging = false;
    boolean isChargingByHotBillingCharging = false;
    ChargingCharacteristics chargingCharacteristics = new ChargingCharacteristicsImpl(isNormalCharging, isPrepaidCharging, isFlatRateCharging, isChargingByHotBillingCharging);
    Ext2QoSSubscribed_SourceStatisticsDescriptor sourceStatisticsDescriptor = Ext2QoSSubscribed_SourceStatisticsDescriptor.unknown;
    boolean optimisedForSignallingTraffic = true;
    int maxBRDLExt = 256000;
    ExtQoSSubscribed_BitRateExtended maxBitRateForDLExt = new ExtQoSSubscribed_BitRateExtendedImpl(maxBRDLExt, isSourceData);
    int gbrExtDL = 128000;
    ExtQoSSubscribed_BitRateExtended guaranteedBitRateForDLExtended = new ExtQoSSubscribed_BitRateExtendedImpl(gbrExtDL, isSourceData);
    Ext2QoSSubscribed ext2QoSSubscribed = new Ext2QoSSubscribedImpl(sourceStatisticsDescriptor, optimisedForSignallingTraffic,
        maxBitRateForDLExt, guaranteedBitRateForDLExtended);
    int mbrULExt = 256000;
    ExtQoSSubscribed_BitRateExtended maximumBitRateForUplinkExtended = new ExtQoSSubscribed_BitRateExtendedImpl(mbrULExt, isSourceData);
    int gbrULExt = 128000;
    ExtQoSSubscribed_BitRateExtended guaranteedBitRateForUplinkExtended = new ExtQoSSubscribed_BitRateExtendedImpl(gbrULExt, isSourceData);
    Ext3QoSSubscribed ext3QoSSubscribed = new Ext3QoSSubscribedImpl(maximumBitRateForUplinkExtended, guaranteedBitRateForUplinkExtended);
    Ext4QoSSubscribed ext4QoSSubscribed = new Ext4QoSSubscribedImpl(91);
    APNOIReplacement apnOiReplacement = new APNOIReplacementImpl(new byte[] { 81, 92, 83, 84, 85, 86, 87, 88, 89 });
    ExtPDPType extpdpType = new ExtPDPTypeImpl(new byte[] { 58, 59 });
    PDPAddress extpdpAddress = new PDPAddressImpl(new byte[] { 60 });
    SIPTOPermission sipToPermission = SIPTOPermission.siptoAllowed;
    LIPAPermission lipaPermission = LIPAPermission.lipaConditional;
    PDPContext pdpContext = new PDPContextImpl(pdpContextId, pdpType, pdpAddress, qosSubscribed, vplmnAddressAllowed, apn,
        null, extQoSSubscribed, chargingCharacteristics, ext2QoSSubscribed, ext3QoSSubscribed, ext4QoSSubscribed,
        apnOiReplacement, extpdpType, extpdpAddress, sipToPermission, lipaPermission);
    gprsDataList.add(pdpContext);
    return new GPRSSubscriptionDataImpl(completeDataListIncluded, gprsDataList, null, apnOiReplacement);
  }
}
