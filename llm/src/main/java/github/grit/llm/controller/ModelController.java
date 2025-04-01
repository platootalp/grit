package github.grit.llm.controller;

import java.util.List;

import github.grit.llm.facade.response.ModelResponse;
import github.grit.llm.service.IModelService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model")
@RequiredArgsConstructor
public class ModelController {

	private final IModelService modelService;

	/**
	 * 获取所有模型列表（系统内置 + 用户自定义）
	 */
	@GetMapping("/s/{userId}")
	public List<ModelResponse> listModels(@PathVariable Long userId) {
		return modelService.getModelList(userId);
	}

//	/**
//	 * 添加自定义模型
//	 */
//	@PostMapping("/add")
//	public ResponseEntity<String> addModel(@RequestBody ModelRequest request) {
//		modelService.addModel(request);
//		return ResponseEntity.ok("模型添加成功");
//	}
//
//	/**
//	 * 修改模型信息
//	 */
//	@PutMapping("/update/{modelId}")
//	public ResponseEntity<String> updateModel(@PathVariable Long modelId, @RequestBody ModelRequest request) {
//		modelService.updateModel(modelId, request);
//		return ResponseEntity.ok("模型修改成功");
//	}
//
//	/**
//	 * 删除模型
//	 */
//	@DeleteMapping("/delete/{modelId}")
//	public ResponseEntity<String> deleteModel(@PathVariable Long modelId) {
//		modelService.deleteModel(modelId);
//		return ResponseEntity.ok("模型删除成功");
//	}
//
//	/**
//	 * 进行模型推理
//	 */
//	@PostMapping("/infer/{modelId}")
//	public ModelInferenceResponse infer(@PathVariable Long modelId, @RequestBody ModelInferenceRequest request) {
//		return modelService.infer(modelId, request);
//	}
//
//	/**
//	 * 获取模型调用日志
//	 */
//	@GetMapping("/logs")
//	public List<ModelLogResponse> getModelLogs() {
//		return modelService.getModelLogs();
//	}
}

