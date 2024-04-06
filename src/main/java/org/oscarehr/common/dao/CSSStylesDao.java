package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.CssStyle;

public interface CSSStylesDao extends AbstractDao<CssStyle> {
    List<CssStyle> findAll();
}
