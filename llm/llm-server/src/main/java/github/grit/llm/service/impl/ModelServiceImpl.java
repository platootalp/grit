package github.grit.llm.service.impl;

import java.util.List;

import github.grit.llm.facade.response.ModelResponse;
import github.grit.llm.service.IModelService;

import org.springframework.stereotype.Service;

@Service
public class ModelServiceImpl implements IModelService {
	@Override
	public List<ModelResponse> getModelList(Long userId) {
		return List.of();
	}
}
