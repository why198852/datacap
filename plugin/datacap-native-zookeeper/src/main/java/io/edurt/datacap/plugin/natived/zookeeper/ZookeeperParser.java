package io.edurt.datacap.plugin.natived.zookeeper;

import io.edurt.datacap.spi.parser.SqlParser;
import io.edurt.datacap.sql.SqlBase;
import io.edurt.datacap.sql.SqlBaseToken;
import org.apache.commons.lang3.StringUtils;

public class ZookeeperParser
        extends SqlParser
{
    public ZookeeperParser(String content)
    {
        super(content);
    }

    @Override
    public String getExecuteContext()
    {
        SqlBase sqlBase = this.getSqlBase();
        if (sqlBase.getToken() == SqlBaseToken.SHOW) {
            if (StringUtils.isEmpty(sqlBase.getTable())) {
                return ZookeeperPathConvert.start;
            }
            return ZookeeperPathConvert.toPath(sqlBase.getTable());
        }
        else if (sqlBase.getToken() == SqlBaseToken.SELECT) {
            return ZookeeperPathConvert.toPath(sqlBase.getTable());
        }
        return null;
    }
}
