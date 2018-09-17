package com.biz.smarthard.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sdk.core.db.MySqlDao;
import com.sdk.core.json.JsonUtil;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import snowfox.lang.util.Convert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class JacksonUtil {
    
    public interface BeanOnBean {

        void setBean(Object obj, Class<?> clazz);

    }
    
    public static JacksonUtil jackson = new JacksonUtil();

    private static volatile XmlMapper xmlMapper;

    // 单例模式
    private static XmlMapper getXmlMapper() {
        if (xmlMapper == null) {
            synchronized (JacksonUtil.class) {
                if (xmlMapper == null) {
                    xmlMapper = new XmlMapper();
                }
            }
        }
        return xmlMapper;
    }

    // 模式
    private ObjectMapper mapper;

    // 单例模式
    private ObjectMapper getMapper() {
        if (mapper == null) {
            synchronized (JacksonUtil.class) {
                if (mapper == null) {
                    mapper = new ObjectMapper();
                }
            }
        }
        return mapper;
    }

    /**
     * Float反序列化成BigDECIMAL
     * 
     * @return
     */
    public JacksonUtil witDishBigForFloats() {
        getMapper().disable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        return this;
    }

    /**
     * 反序列化允许null
     * 
     * @return
     */
    public JacksonUtil withDisAcceptNull() {
        getMapper().disable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        return this;
    }

    /**
     * 反序列化允许null,String ""
     * 
     * @return
     */
    public JacksonUtil withDisAcceptStringNull() {
        getMapper().disable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        return this;
    }

    /**
     * Float强转成int
     * 
     * @return
     */
    public JacksonUtil withDisFloatAsInt() {
        getMapper().disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);
        return this;
    }

    /**
     * String[]强转成数组
     * 
     * @return
     */
    public JacksonUtil withDisStringAsArray() {
        getMapper().disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return this;
    }

    /**
     * date转化成timeZone
     * 
     * @return
     */
    public JacksonUtil withDisDataAsTimeZone() {
        getMapper().disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        return this;
    }

    public JacksonUtil withDisFETCH() {
        getMapper().disable(DeserializationFeature.EAGER_DESERIALIZER_FETCH);
        return this;
    }

    /**
     * 失败忽略
     * 
     * @return
     */
    public JacksonUtil withDisFailIgnoged() {
        getMapper().disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        return this;
    }

    /**
     * 未知属性
     * 
     * @return
     */
    public JacksonUtil withDisFailUnknow() {
        getMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return this;
    }

    /**
     * 没定义的属性忽略
     * 
     * @return
     */
    public JacksonUtil withIgnoreUnknow() {
        getMapper().enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
        return this;
    }

    /**
     * 没定义的属性忽略
     * 
     * @return
     */
    public JacksonUtil withIgnoreUnknowPro() {
        getMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return this;
    }

    /**
     * 用科学计数法表示
     * 
     * @return
     */
    public JacksonUtil withBigAsPlain() {
        getMapper().enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        return this;
    }

    /**
     *           *确定解析器是否允许使用的功能           *未引用的字段名称（由Javascript允许，          
     * *但不是JSON规范）。           *由于JSON规范要求使用双引号           *字段名称，          
     * *这是非标准功能，默认情况下禁用。          
     */
    public JacksonUtil withAllowFieldNames() {
        getMapper().enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        return this;
    }

    /**
     * 确定解析器是否允许的功能 JSON字符串包含无引号的控制字符（ASCII字符的值小于32，包括标签和换行字符）。
     * 如果将feature设置为false，则会抛出异常遇到字符由于JSON规范要求引用所有控制字符，这是非标准功能，默认情况下禁用。
     * 
     * @return
     */
    public JacksonUtil withAllowControlChars() {
        getMapper().enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        return this;
    }

    /**
     * 确定解析器是否允许使用的功能单引号（撇号，字符“\”）为引用字符串（名称和字符串值）。
     * 如果是这样，这是除了其他可接受的标记之外。但不是JSON规范）。 由于JSON规范要求使用双引号字段名称，这是非标准功能，默认情况下禁用。
     * 
     * @return
     */
    public JacksonUtil withAllowSingleQuotes() {
        getMapper().enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        return this;
    }

    public JacksonUtil withDefualtTyping(TypeResolverBuilder<?> typeResolverBuilder) {
        typeResolverBuilder.init(JsonTypeInfo.Id.CLASS, null);
        typeResolverBuilder.inclusion(JsonTypeInfo.As.PROPERTY);
        getMapper().setDefaultTyping(typeResolverBuilder);
        return this;
    }

    /**
     * 伪值用于表示较高级别的默认值 意义上，避免超越包容性价值。 例如，如果返回           对于属性，这将使用包含的类的默认值
     * 财产，如有任何定义; 如果没有定义，那么 全局序列化包含细节。
     * 
     * @return
     */
    public JacksonUtil withUseDefaults() {
        getMapper().setSerializationInclusion(Include.USE_DEFAULTS);
        return this;
    }

    /**
     * 表示所有的属性
     * 
     * @return
     */
    public JacksonUtil withAll() {
        getMapper().setSerializationInclusion(Include.ALWAYS);
        return this;
    }

    /**
     * 表示只有具有值的属性
     * 
     * @return
     */
    public JacksonUtil withNotEmpty() {
        getMapper().setSerializationInclusion(Include.NON_EMPTY);
        return this;
    }

    /**
     * 通常可以构建专门的文本对象的方法
     * 
     * @return
     */
    public JacksonUtil withOverrideAccess() {
        getMapper().getSerializationConfig().canOverrideAccessModifiers();
        return this;
    }

    /**
     * 访问者确定是否可以尝试强制重写访问
     * 
     * @param src//要表示的文本
     * @return
     */
    public JacksonUtil withCompileString(String src) {
        getMapper().getSerializationConfig().compileString(src);
        return this;
    }

    /**
     * 配置对象工厂
     * 
     * @return
     */
    public JacksonUtil withConstruct() {
        getMapper().getSerializationConfig().constructDefaultPrettyPrinter();
        return this;
    }

    /**
     * 设置跟节点名称
     * 
     * @return
     */
    public JacksonUtil withRootName(String rootName) {
        getMapper().getSerializationConfig().withRootName(rootName);
        return this;
    }

    /**
     * 表示仅属性为非空的值
     * 
     * @return
     */
    public JacksonUtil withNotNull() {
        getMapper().setSerializationInclusion(Include.NON_NULL);
        return this;
    }

    /**
     * 是否缩放排列输出
     * 
     * @param //isOrder
     * @return
     */
    public JacksonUtil withOrder() {
        getMapper().configure(SerializationFeature.INDENT_OUTPUT, true);
        return this;
    }

    /**
     * 是否环绕根元素(以类名作为根元素) 默认是true
     * 
     * @param //isRoot
     * @return
     */
    public JacksonUtil withRoot() {
        getMapper().configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        return this;
    }

    /**
     * 将下划线转化成驼峰
     * 
     * @return
     */
    public JacksonUtil withCamel2Lower() {
        getMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return this;
    }

    /**
     * 将首字母小写转化为大写
     * 
     * @return
     */
    public JacksonUtil with2CamelCase() {
        getMapper().setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        return this;
    }

    /**
     * 转化成全小写
     * 
     * @return
     */
    public JacksonUtil with2Lower() {
        getMapper().setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE);
        return this;
    }

    /**
     * 序列化日期时以timestamps
     * 
     * @param //isTimestamps
     * @return
     */
    public JacksonUtil withTimestamps() {
        getMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        return this;
    }

    /**
     * 将枚举以String输出
     * 
     * @return
     */
    public JacksonUtil withEnum2String() {
        getMapper().configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, false);
        return this;
    }

    /**
     * 将枚举以Ordinal输出
     * 
     * @return
     */
    public JacksonUtil withEnum2Ordinal() {
        getMapper().configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
        return this;
    }

    /**
     * 单个元素的数组不以数组输出
     * 
     * @return
     */
    public JacksonUtil withArray() {
        getMapper().configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true);
        return this;
    }

    /**
     * 序列化Map时对key进行排序操作
     * 
     * @return
     */
    public JacksonUtil withMapOrder() {
        getMapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        return this;
    }

    /**
     * 序列化char[]时以json数组输出
     * 
     * @return
     */
    public JacksonUtil withChar() {
        getMapper().configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true);
        return this;
    }

    public <T> JacksonUtil withType(Class<T> type, Class<?>... parameterClasses) {
        getMapper().getTypeFactory().constructParametricType(type, parameterClasses);
        return this;
    }

    /**
     * 将Object对象转化成json
     * 
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public String obj2Json(Object obj) throws JsonProcessingException {
        return getMapper().writeValueAsString(obj);
    }

    /**
     * 将Object对象转化成byte数组
     * 
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public byte[] obj2Byte(Object obj) throws JsonProcessingException {
        return getMapper().writeValueAsBytes(obj);
    }

    /**
     * 将json转化成Obj
     * 
     * @param json
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public <T> T json2Obj(String json)
            throws JsonParseException, JsonMappingException, IOException {
        return getMapper().readValue(json, new TypeReference<Object>() {});
    }

    /**
     * 将byte数组转换成Obj
     * 
     * @param by
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public <T> T byte2Obj(byte[] by) throws JsonParseException, JsonMappingException, IOException {
        return getMapper().readValue(by, new TypeReference<Object>() {});
    }

    /**
     * 将json转化成bean对象
     * 
     * @param json
     * @param t
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public <T> T json2Obj(String json, Class<T> t)
            throws JsonParseException, JsonMappingException, IOException {
        return getMapper().readValue(json, t);
    }

    /**
     * 将obj转换成对象
     * 
     * @param obj
     * @param t
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws JsonProcessingException
     * @throws IOException
     */
    public <T> T obj2Bean(Object obj, Class<T> t)
            throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
        return getMapper().readValue(getMapper().writeValueAsString(obj), t);
    }
    
    /**
     * 将obj转换成对象
     * 
     * @param //<T>
     * 
     * @param obj
     * @param //t
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws JsonProcessingException
     * @throws IOException
     */
    public BeanOnBean obj2Bean(Object obj,
                               Class<? extends BeanOnBean> type,
                               Class<?>... parameterClasses)
            throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
        BeanOnBean t = obj2Bean(obj, type);
        for (Class<?> clazz : parameterClasses) {
            t.setBean(obj2Bean(obj, clazz), clazz);
        }
        return t;
    }

    /**
     * 将byte数组转换成对象
     * 
     * @param src
     * @param t
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public <T> T byte2Bean(byte[] src, Class<T> t)
            throws JsonParseException, JsonMappingException, IOException {
        return getMapper().readValue(src, t);
    }

    /**
     * 将Object对象转化成xml
     * 
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public static String obj2Xml(Object obj) throws JsonProcessingException {
        return getXmlMapper().writeValueAsString(obj);
    }

    /**
     * 将xml转化成Obj
     * 
     * @param //json
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static <T> T xml2Obj(String xml) throws JsonParseException, JsonMappingException, IOException {
        return getXmlMapper().readValue(xml, new TypeReference<Object>() {});
    }

    /**
     * 将xml转化成bean对象
     * 
     * @param //json
     * @param t
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public <T> T xml2Obj(String xml, Class<T> t)
            throws JsonParseException, JsonMappingException, IOException {
        return getXmlMapper().readValue(xml, t);
    }

    public void printJson(Object obj) throws JsonProcessingException {
        System.out.println(obj2Json(obj));
    }

    public void printJson(String json) throws IOException {
        System.out.println(obj2Json(json2Obj(json)));
    }

    public void printXml(String xml) throws IOException {
        System.out.println(obj2Json(xml2Obj(xml)));
    }

    public void printXml(Object obj) throws JsonProcessingException {
        System.out.println(obj2Xml(obj));
    }

    /**
     * 清楚mapper
     */
    public void clear() {
        if (getMapper() != null) {
            synchronized (this) {
                if (getMapper() != null) {
                    this.mapper = null;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, JDOMException, SQLException {
        Object[] param = {"app120180503144741616"};
        Map<String,Object> queryMap = MySqlDao.getDao().queryOne("SELECT * FROM sh_pay_trade WHERE out_trade_no=?",param);
        SmartHardUtil.checkPackages(queryMap);
        String json = Convert.toString(queryMap.get("packages"));
        List<Map<String,Object>> list = JacksonUtil.jackson.json2Obj(json);
        System.out.println(list);
        //Map<String,Object> json = JacksonUtil.xml2Obj(xml);
        //System.out.println(json);
        //
        //String mapStr = "{\n" +
        //        "\t\t\"domain\":\"www.adflash.cn\",\n" +
        //        "\t\t\"name\":\"adflash\",\n" +
        //        "\t\t\"bundle\":\"com.adflash\"\n" +
        //        "\t\t}";
        //Map<String,Object> map = JsonUtil.jsonToObject(mapStr);
        //String result = JacksonUtil.obj2Xml(map);
        //System.out.println(result);
        //System.out.println(System.currentTimeMillis());
    }

    private static Map<String,Object> xml2Json(String xml) throws JDOMException, IOException {
        JSONObject json = new JSONObject();
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        SAXBuilder sb = new SAXBuilder();
        org.jdom2.Document doc = sb.build(is);
        Element root = doc.getRootElement();
        json.put(root.getName(), iterateElement(root));
        String result = json.toString();
        return JsonUtil.jsonToObject(result);
    }

    private static JSONObject iterateElement(Element element) {
        List node = element.getChildren();
        Element et = null;
        JSONObject obj = new JSONObject();
        for (int i = 0; i < node.size(); i++) {
            et = (Element) node.get(i);
            if (et.getTextTrim().equals("")) {
                if (et.getChildren().size() == 0) {
                    continue;
                }
                obj.put(et.getName(), iterateElement(et));
            } else {
                obj.put(et.getName(), et.getTextTrim());
            }
        }
        return obj;
    }
}
