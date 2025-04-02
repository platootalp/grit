package github.grit.llm.service;

import java.util.List;

import github.grit.llm.facade.response.ModelResponse;

public interface IModelService {

	List<ModelResponse> getModelList(Long userId);
}
