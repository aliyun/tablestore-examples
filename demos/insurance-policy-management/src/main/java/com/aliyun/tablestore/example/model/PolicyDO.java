package com.aliyun.tablestore.example.model;

import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyColumn;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.aliyun.tablestore.example.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.aliyun.tablestore.example.consts.ColumnConsts.APPLIER_GENDER;
import static com.aliyun.tablestore.example.consts.ColumnConsts.APPLIER_ID;
import static com.aliyun.tablestore.example.consts.ColumnConsts.APPLIER_NAME;
import static com.aliyun.tablestore.example.consts.ColumnConsts.APPLIER_USER_ID;
import static com.aliyun.tablestore.example.consts.ColumnConsts.BENEFICIARY_INFO;
import static com.aliyun.tablestore.example.consts.ColumnConsts.BENEFIT_PERCENTAGE;
import static com.aliyun.tablestore.example.consts.ColumnConsts.BROKER_USER_ID;
import static com.aliyun.tablestore.example.consts.ColumnConsts.EFFECTIVE_TIME;
import static com.aliyun.tablestore.example.consts.ColumnConsts.EXPIRATION_TIME;
import static com.aliyun.tablestore.example.consts.ColumnConsts.GENDER;
import static com.aliyun.tablestore.example.consts.ColumnConsts.ID;
import static com.aliyun.tablestore.example.consts.ColumnConsts.INSURED_GENDER;
import static com.aliyun.tablestore.example.consts.ColumnConsts.INSURED_ID;
import static com.aliyun.tablestore.example.consts.ColumnConsts.INSURED_NAME;
import static com.aliyun.tablestore.example.consts.ColumnConsts.NAME;
import static com.aliyun.tablestore.example.consts.ColumnConsts.OPERATE_TIME;
import static com.aliyun.tablestore.example.consts.ColumnConsts.POLICY_ID;
import static com.aliyun.tablestore.example.consts.ColumnConsts.POLICY_ID_MD5;
import static com.aliyun.tablestore.example.consts.ColumnConsts.PREMIUM;
import static com.aliyun.tablestore.example.consts.ColumnConsts.PRODUCT_NAME;
import static com.aliyun.tablestore.example.consts.ColumnConsts.PROFIT;

/**
 * @author hydrogen
 */
public class PolicyDO {

    public static class BeneficiaryInfo {
        private String name;

        private String id;

        private String gender;

        private Long benefitPercentage;

        public String getName() {
            return name;
        }

        public BeneficiaryInfo setName(String name) {
            this.name = name;
            return this;
        }

        public String getId() {
            return id;
        }

        public BeneficiaryInfo setId(String id) {
            this.id = id;
            return this;
        }

        public String getGender() {
            return gender;
        }

        public BeneficiaryInfo setGender(String gender) {
            this.gender = gender;
            return this;
        }

        public Long getBenefitPercentage() {
            return benefitPercentage;
        }

        public BeneficiaryInfo setBenefitPercentage(Long benefitPercentage) {
            this.benefitPercentage = benefitPercentage;
            return this;
        }
    }

    private String policyId;

    private String productName;

    private Long operateTime;

    private Long effectiveTime;

    private Long expirationTime;

    private String applierUserId;

    private String brokerUserId;

    private String applierName;

    private String applierId;

    private String applierGender;

    private String insuredName;

    private String insuredId;

    private String insuredGender;

    private Long premium;

    private Long profit;

    private List<BeneficiaryInfo> beneficiaryInfo = Collections.emptyList();

    public String getPolicyId() {
        return policyId;
    }

    public PolicyDO setPolicyId(String policyId) {
        this.policyId = policyId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public PolicyDO setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public PolicyDO setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
        return this;
    }

    public Long getEffectiveTime() {
        return effectiveTime;
    }

    public PolicyDO setEffectiveTime(Long effectiveTime) {
        this.effectiveTime = effectiveTime;
        return this;
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    public PolicyDO setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    public String getApplierUserId() {
        return applierUserId;
    }

    public PolicyDO setApplierUserId(String applierUserId) {
        this.applierUserId = applierUserId;
        return this;
    }

    public String getBrokerUserId() {
        return brokerUserId;
    }

    public PolicyDO setBrokerUserId(String brokerUserId) {
        this.brokerUserId = brokerUserId;
        return this;
    }

    public String getApplierName() {
        return applierName;
    }

    public PolicyDO setApplierName(String applierName) {
        this.applierName = applierName;
        return this;
    }

    public String getApplierId() {
        return applierId;
    }

    public PolicyDO setApplierId(String applierId) {
        this.applierId = applierId;
        return this;
    }

    public String getInsuredName() {
        return insuredName;
    }

    public PolicyDO setInsuredName(String insuredName) {
        this.insuredName = insuredName;
        return this;
    }

    public String getInsuredId() {
        return insuredId;
    }

    public PolicyDO setInsuredId(String insuredId) {
        this.insuredId = insuredId;
        return this;
    }

    public String getInsuredGender() {
        return insuredGender;
    }

    public PolicyDO setInsuredGender(String insuredGender) {
        this.insuredGender = insuredGender;
        return this;
    }

    public Long getPremium() {
        return premium;
    }

    public PolicyDO setPremium(Long premium) {
        this.premium = premium;
        return this;
    }

    public Long getProfit() {
        return profit;
    }

    public PolicyDO setProfit(Long profit) {
        this.profit = profit;
        return this;
    }

    public List<BeneficiaryInfo> getBeneficiaryInfo() {
        return beneficiaryInfo;
    }

    public PolicyDO setBeneficiaryInfo(List<BeneficiaryInfo> beneficiaryInfo) {
        if (null == beneficiaryInfo) {
            beneficiaryInfo = Collections.emptyList();
        }
        this.beneficiaryInfo = beneficiaryInfo;
        return this;
    }

    public String getApplierGender() {
        return applierGender;
    }

    public PolicyDO setApplierGender(String applierGender) {
        this.applierGender = applierGender;
        return this;
    }

    public RowPutChange toRowPutChange(String tableName) {
        RowPutChange rowPutChange = new RowPutChange(tableName);
        // primary key
        PrimaryKey primaryKey = new PrimaryKey(new PrimaryKeyColumn[]{
                new PrimaryKeyColumn(POLICY_ID_MD5, PrimaryKeyValue.fromString(DigestUtils.md5Hex(policyId)))
        });
        rowPutChange.setPrimaryKey(primaryKey);
        // value columns
        rowPutChange.addColumn(POLICY_ID, ColumnValue.fromString(policyId))
                .addColumn(PRODUCT_NAME, ColumnValue.fromString(productName))
                .addColumn(OPERATE_TIME, ColumnValue.fromLong(operateTime))
                .addColumn(EFFECTIVE_TIME, ColumnValue.fromLong(effectiveTime))
                .addColumn(EXPIRATION_TIME, ColumnValue.fromLong(expirationTime))
                .addColumn(APPLIER_USER_ID, ColumnValue.fromString(applierUserId))
                .addColumn(BROKER_USER_ID, ColumnValue.fromString(brokerUserId))
                .addColumn(APPLIER_NAME, ColumnValue.fromString(applierName))
                .addColumn(APPLIER_ID, ColumnValue.fromString(applierId))
                .addColumn(APPLIER_GENDER, ColumnValue.fromString(applierGender))
                .addColumn(INSURED_NAME, ColumnValue.fromString(insuredName))
                .addColumn(INSURED_ID, ColumnValue.fromString(insuredId))
                .addColumn(INSURED_GENDER, ColumnValue.fromString(insuredGender))
                .addColumn(PREMIUM, ColumnValue.fromLong(premium))
                .addColumn(PROFIT, ColumnValue.fromLong(profit));
        // nested field beneficiaryInfo, save as JSON array in Tablestore
        ArrayNode arrayNode = Utils.OBJECT_MAPPER.createArrayNode();
        for (BeneficiaryInfo beneficiaryInfo : beneficiaryInfo) {
            ObjectNode objectNode = Utils.OBJECT_MAPPER.createObjectNode();
            objectNode.put(NAME, beneficiaryInfo.getName())
                    .put(ID, beneficiaryInfo.getId())
                    .put(GENDER, beneficiaryInfo.getGender())
                    .put(BENEFIT_PERCENTAGE, beneficiaryInfo.getBenefitPercentage());
            arrayNode.add(objectNode);
        }
        rowPutChange.addColumn(BENEFICIARY_INFO, ColumnValue.fromString(arrayNode.toString()));

        return rowPutChange;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getLatestColumnValue(Row row, String name, Class<T> valueType) {
        Column column = row.getLatestColumn(name);

        if (String.class == valueType) {
            return (T) column.getValue().asString();
        } else if (Long.class == valueType || long.class == valueType) {
            return (T) Long.valueOf(column.getValue().asLong());
        } else if (boolean.class == valueType || Boolean.class == valueType) {
            return (T) Boolean.valueOf(column.getValue().asBoolean());
        } else if (double.class == valueType || Double.class == valueType) {
            return (T) Double.valueOf(column.getValue().asDouble());
        }
        throw new UnsupportedOperationException("Unknown type " + valueType);
    }

    public static PolicyDO fromRow(Row row) {
        PolicyDO policyDO = new PolicyDO();
        policyDO.setPolicyId(getLatestColumnValue(row, POLICY_ID, String.class));
        policyDO.setProductName(getLatestColumnValue(row, PRODUCT_NAME, String.class));
        policyDO.setOperateTime(getLatestColumnValue(row, OPERATE_TIME, Long.class));
        policyDO.setEffectiveTime(getLatestColumnValue(row, EFFECTIVE_TIME, Long.class));
        policyDO.setExpirationTime(getLatestColumnValue(row, EXPIRATION_TIME, Long.class));
        policyDO.setApplierUserId(getLatestColumnValue(row, APPLIER_USER_ID, String.class));
        policyDO.setBrokerUserId(getLatestColumnValue(row, BROKER_USER_ID, String.class));
        policyDO.setApplierName(getLatestColumnValue(row, APPLIER_NAME, String.class));
        policyDO.setApplierId(getLatestColumnValue(row, APPLIER_ID, String.class));
        policyDO.setApplierGender(getLatestColumnValue(row, APPLIER_GENDER, String.class));
        policyDO.setInsuredName(getLatestColumnValue(row, INSURED_NAME, String.class));
        policyDO.setInsuredId(getLatestColumnValue(row, INSURED_ID, String.class));
        policyDO.setInsuredGender(getLatestColumnValue(row, INSURED_GENDER, String.class));
        policyDO.setPremium(getLatestColumnValue(row, PREMIUM, Long.class));
        policyDO.setProfit(getLatestColumnValue(row, PROFIT, Long.class));

        // beneficiary info
        String beneficiaryInfoJson = getLatestColumnValue(row, BENEFICIARY_INFO, String.class);
        JsonNode json;
        try {
            json = Utils.OBJECT_MAPPER.readTree(beneficiaryInfoJson);
        } catch (IOException e) {
            throw new IllegalStateException(BENEFICIARY_INFO + " is not json");
        }
        List<BeneficiaryInfo> beneficiaryInfoList = new ArrayList<>(json.size());
        policyDO.setBeneficiaryInfo(beneficiaryInfoList);
        for (JsonNode singleInfo : json) {
            BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo();
            beneficiaryInfo.setName(singleInfo.get(NAME).asText());
            beneficiaryInfo.setId(singleInfo.get(ID).asText());
            beneficiaryInfo.setGender(singleInfo.get(GENDER).asText());
            beneficiaryInfo.setBenefitPercentage(singleInfo.get(BENEFIT_PERCENTAGE).asLong());
            beneficiaryInfoList.add(beneficiaryInfo);
        }
        return policyDO;
    }

    @Override
    public String toString() {
        try {
            return Utils.OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
