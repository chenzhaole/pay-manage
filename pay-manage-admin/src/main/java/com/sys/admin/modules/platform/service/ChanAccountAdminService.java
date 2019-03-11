package com.sys.admin.modules.platform.service;

import com.sys.core.dao.dmo.CaChanAccountDetail;
import java.util.List;

public interface ChanAccountAdminService {
	List<CaChanAccountDetail> list(CaChanAccountDetail caChanAccountDetail);
	int count(CaChanAccountDetail caChanAccountDetail);
}
