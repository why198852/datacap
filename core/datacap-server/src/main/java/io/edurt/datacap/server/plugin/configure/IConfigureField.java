package io.edurt.datacap.server.plugin.configure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IConfigureField
{
    private IConfigureFieldName field; // The field name of the tag configuration
    private IConfigureFieldType type;
    private boolean required;
    private Long min;
    private Long max;
    private String message;
    private Object value;
    private IConfigureFieldGroup group = IConfigureFieldGroup.configure;
}
