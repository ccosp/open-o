package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.CssStyle;

public interface CSSStylesDAO extends AbstractDao<CssStyle> {
    List<CssStyle> findAll();
}
