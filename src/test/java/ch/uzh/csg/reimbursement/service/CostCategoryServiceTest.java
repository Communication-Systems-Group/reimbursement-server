package ch.uzh.csg.reimbursement.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.model.exception.CostCategoryNotFoundException;
import ch.uzh.csg.reimbursement.repository.CostCategoryRepositoryProvider;

@RunWith(MockitoJUnitRunner.class)
public class CostCategoryServiceTest {

	@InjectMocks
	private CostCategoryService service;

	@Mock
	private CostCategoryRepositoryProvider repository;

	@Test
	public void testGetAll() {

		// given
		List<CostCategory> list = new ArrayList<CostCategory>();
		given(repository.findAll()).willReturn(list);

		// when
		List<CostCategory> returningList = service.getAll();

		// then
		assertThat(returningList, is(equalTo(list)));

	}

	@Test(expected = CostCategoryNotFoundException.class)
	public void testGetByUidIfNoCostCategoryIsFound() {

		// given
		String uid = "cost-category-id";
		given(repository.findByUid(uid)).willReturn(null);

		// when
		service.getByUid(uid);

		// then
		// above mentioned exception is thrown
	}

	@Test
	public void testGetByUidIfACostCategoryIsFound() {

		// given
		String uid = "fancy-user-id";
		CostCategory costCategory = mock(CostCategory.class);
		given(repository.findByUid(uid)).willReturn(costCategory);

		// when
		CostCategory returningCostCategory = service.getByUid(uid);

		// then
		assertThat(returningCostCategory, is(equalTo(costCategory)));

	}
}
