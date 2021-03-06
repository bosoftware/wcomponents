define(["wc/has", "wc/mixin", "wc/dom/Widget", "wc/dom/event", "wc/dom/uid", "wc/dom/classList", "wc/timers", "wc/ui/prompt",
	"wc/i18n/i18n", "fabric", "wc/ui/dialogFrame", "wc/template", "wc/ui/ImageCapture", "wc/ui/ImageUndoRedo", "wc/file/getMimeType"],
function(has, mixin, Widget, event, uid, classList, timers, prompt, i18n, fabric, dialogFrame, template, ImageCapture, ImageUndoRedo, getMimeType) {
	var timer,
		imageEdit = new ImageEdit();

	ImageEdit.prototype.mimeToExt = {
		"image/jpeg": "jpeg",
		"image/png": "png",
		"image/webp": "webp"
	};

	ImageEdit.prototype.renderCanvas = function(callback) {
		if (timer) {
			timers.clearTimeout(timer);
		}
		timer = timers.setTimeout(function() {
			var fbCanvas = imageEdit.getCanvas();
			fbCanvas.renderAll();
			if (callback) {
				callback();
			}
		}, 50);
	};

	ImageEdit.prototype.defaults = {
		width: 320,
		height: 240,
		format: "png",  // png or jpeg
		quality: 1,  // only if format is jpeg
		multiplier: 1,
		face: false,
		rotate: true,
		zoom: true,
		move: true,
		redact: false,
		reset: true,
		undo: true,
		cancel: true,
		save: true,
		crop: true
	};

	/**
	 * This provides a mechanism to allow the user to edit images during the upload process.
	 * It may also be used to edit static images after they have been uploaded as long as a file uploader is configured to take the edited images.
	 *
	 * @constructor
	 */
	function ImageEdit() {

		var inited,
			TEMPLATE_NAME = "imageEdit.xml",
			imageCapture = new ImageCapture(this),
			overlayUrl,
			undoRedo,
			registeredIds = {},
			fbCanvas,
			BUTTON = new Widget("button");

		this.getCanvas = function() {
			return fbCanvas;
		};

		/**
		 * Registers a configuration object against a unique ID to specificy variables such as overlay image URL, width, height etc.
		 *
		 * @param {Object[]} arr Configuration objects.
		 */
		this.register = function(arr) {
			var i, next;
			for (i = 0; i < arr.length; i++) {
				next = arr[i];
				registeredIds[next.id] = next;
			}
			if (!inited) {
				inited = true;
				require(["wc/dom/initialise", "wc/ui/multiFileUploader"], function(initialise, multiFileUploader) {
					initialise.addCallback(function(element) {
						event.add(element, "click", clickEvent);
					});

					/**
					 * Listens for edit requests on static images.
					 * @param {Event} $event A click event.
					 */
					function clickEvent($event) {
						var img, uploader, file, id,
							element = BUTTON.findAncestor($event.target);
						if (element) {
							id = element.getAttribute("data-wc-selector");
							if (id && element.localName === "button") {
								uploader = document.getElementById(id);
								if (uploader) {
									img = document.getElementById(element.getAttribute("data-wc-img"));
									if (img) {
										file = imgToFile(img);
										multiFileUploader.upload(uploader, [file]);
									} else {
										var win = function(files) {
											multiFileUploader.upload(uploader, files, true);
										};
										var lose = function(message) {
											if (message) {
												prompt.alert(message);
											}
										};
										imageEdit.editFiles({
											id: id,
											name: element.getAttribute("data-wc-editor")
										}, win, lose);
									}
								}
							}
						}
					}
				});
			}
		};

		/**
		 * Retrieve a configuration object.
		 * @param {Object} obj Get the configuration registered for the "id" or "name" property of this object (in that order).
		 * @returns {Object} configuration
		 */
		this.getConfig = function(obj) {
			var editorId, result, defaultConfig;
			if (obj) {
				result = registeredIds[obj.id] || registeredIds[obj.name];
				if (!result) {
					if ("getAttribute" in obj) {
						editorId = obj.getAttribute("data-wc-editor");
					} else {
						editorId = obj.editorId;
					}
					if (editorId) {
						result = registeredIds[editorId];
					}
				}
			}
			if (!result || !result.__wcmixed) {
				defaultConfig = mixin(this.defaults);  // make a copy of defaults;
				result = mixin(result, defaultConfig);  // override defaults with explicit settings
				result.__wcmixed = true;  // flag that we have mixed in the defaults so it doesn't need to happen again
			}
			return result;
		};

		/**
		 * Prompt the user to edit the image files.
		 *
		 * If other (non-image) files are present they will be passed through unchanged.
		 * If more than one image file is present the editor will be displayed for each image file one after the other.
		 * If the edit operation is aborted at any point for any file then the entire edit process is aborted (the promise will reject).
		 *
		 *
		 * @param {Object} obj An object with a "files" property that references an array of File blobs to be edited and a registered "id" or "name".
		 * @param {Function} onSuccess Called with an array of File blobs that have potentially been edited by the user.
		 * @param {Function} onError called if something goes wrong.
		 */
		this.editFiles = function(obj, onSuccess, onError) {
			var config = imageEdit.getConfig(obj);
			var file, idx = 0, result = [], files = obj.files,
				done = function(arg) {
					try {
						onSuccess(arg);
					} finally {
						dialogFrame.close();
					}
				};
			try {
				if (files) {
					if (has("dom-canvas")) {
						editNextFile();
					} else {
						done(files);
					}
				} else if (config.camera) {
					editFile(config, null, saveEditedFile, onError);
				}
			} catch (ex) {
				onError(ex);
			}

			/*
			 * Once the user has commited their changes buffer the result and see if there is another file queued for editing.
			 */
			function saveEditedFile(fileToSave) {
				result.push(fileToSave);
				editNextFile();
			}

			/*
			 * Prompt the user to edit the next file in the queue.
			 */
			function editNextFile() {
				if (files && idx < files.length) {
					file = files[idx++];
					if (file.type.indexOf("image/") === 0) {
						editFile(config, file, saveEditedFile, onError);
					} else {
						saveEditedFile(file);
					}
				} else {
					done(result);
				}
			}
		};

		/**
		 * Callback is called with the edited image when editing completed.
		 * @param {Object} config Options for the image editor
		 * @param {File} file The image to edit.
		 * @param {function} win callback on success (passed a File)
		 * @param {function} lose callback on error
		 */
		function editFile(config, file, win, lose) {
			var callbacks = {
					win: win,
					lose: lose
				},
				gotEditor = function() {
					var fileReader;
					fbCanvas = new fabric.Canvas("wc_img_canvas", {
						enableRetinaScaling: false
					});
					fbCanvas.setWidth(config.width);
					fbCanvas.setHeight(config.height);
//					fbCanvas.on("selection:cleared", function() {
//						if (fbImage) {
//							fbCanvas.setActiveObject(fbImage);
//						}
//					});
					overlayUrl = config.overlay;
					if (file) {
						fileReader = new FileReader();
						fileReader.onload = filereaderLoaded;
						fileReader.readAsDataURL(file);
					} else {
						imageCapture.play({
							width: fbCanvas.getWidth(),
							height: fbCanvas.getHeight()
						});
					}
				};
			if (config.face) {
				require(["wc/ui/facetracking"], function(facetracking) {
					callbacks.validate = facetracking.getValidator(config);
					getEditor(config, callbacks, file).then(gotEditor);
				});
			} else if (config.redact) {
				require(["wc/ui/imageRedact"], function(imageRedact) {
					config.redactor = imageRedact;
					getEditor(config, callbacks, file).then(function() {
						gotEditor();
						config.redactor.activate(imageEdit);
					});
				});
			} else {
				getEditor(config, callbacks, file).then(gotEditor);
			}
		}

		/*
		 * Callback invoked when a FileReader instance has loaded.
		 */
		function filereaderLoaded($event) {
			imageEdit.renderImage($event.target.result);
		}

		/**
		 * We're assuming that the image should not scale too small...
		 * This should probably be a config parameter.
		 * @param {number} availWidth The width of the canvas.
		 * @param {number} availHeight The height of the canvas.
		 * @param {number} imgWidth The raw image width.
		 * @param {number} imgHeight The raw image height.
		 * @param {fabric.Image} fbImage The image we are limiting
		 * @returns {number} The minimum scale to keep this image from getting too small.
		 */
		function calcMinScale(availWidth, availHeight, imgWidth, imgHeight, fbImage) {
			var result, minScaleDefault = 0.1, minScaleX, minScaleY,
				minWidth = availWidth * 0.7,
				minHeight = availHeight * 0.7;
			if (imgWidth > minWidth) {
				minScaleX = minWidth / imgWidth;
			} else {
				minScaleX = minScaleDefault;
			}
			if (imgHeight > minHeight) {
				minScaleY = minHeight / imgHeight;
			} else {
				minScaleY = minScaleDefault;
			}
			result = Math.max(minScaleX, minScaleY);
			if (fbImage.scaleX || fbImage.scaleY) {
				// if the image has been auto-scaled already then we should allow it to stay in those parameters
				result = Math.min(fbImage.scaleX, fbImage.scaleY, result);
			}
			return result;
		}

		/*
		 * Displays an img element in the image editor.
		 * @param {Element|string} img An image element or a dataURL.
		 */
		this.renderImage = function(img, callback) {
			var width = fbCanvas.getWidth(),
				height = fbCanvas.getHeight(),
				imageWidth, imageHeight;
			try {
				if (img.nodeType) {
					renderFabricImage(new fabric.Image(img));
				} else {
					fabric.Image.fromURL(img, renderFabricImage);
				}
			} catch (ex) {
				console.warn(ex);
			}
			function renderFabricImage(fabricImage) {
				fabricImage.set({
					angle: 0,
					top: 0,
					left: 0,
					lockScalingFlip: true,
					lockUniScaling: true,
					centeredScaling: true,
					centeredRotation: true
				});
				imageWidth = fabricImage.getWidth();
				imageHeight = fabricImage.getHeight();
				if (imageWidth > imageHeight) {
					// fbCanvas.setZoom(width / imageWidth);
					fabricImage.scaleToWidth(width).setCoords();
				} else {
					// fbCanvas.setZoom(height / imageHeight);
					fabricImage.scaleToHeight(height).setCoords();
				}
				fabricImage.width = imageWidth;
				fabricImage.height = imageHeight;
				calcMinScale(width, height, imageWidth, imageHeight, fabricImage);
				fbCanvas.clear();
				addToCanvas(fabricImage);

				if (overlayUrl) {
					fbCanvas.setOverlayImage(overlayUrl, positionOverlay);
				}
				fbCanvas.renderAll();
				fabricImage.saveState();
				undoRedo = new ImageUndoRedo(imageEdit);
				undoRedo.save();
				if (callback) {
					callback();
				}
			}
		};

		function addToCanvas(object) {
			fbCanvas.add(object);
		}

		this.selectAll = function() {
			var objects = fbCanvas.getObjects().map(function(o) {
				return o.set('active', true);
			});

			var group = new fabric.Group(objects, {
				originX: 'center',
				originY: 'center'
			});

			fbCanvas._activeObject = null;

			fbCanvas.setActiveGroup(group.setCoords()).renderAll();
			return group;
		};

		this.getFbImage = function(container) {
			var objects, currentContainer = (container || fbCanvas);
			if (currentContainer && currentContainer.getObjects) {
				objects = currentContainer.getObjects("image");
				if (objects && objects.length) {
					return objects[0];
				}
				objects = currentContainer.getObjects("group");
				if (objects && objects.length) {
					return objects[0];
				}
//				for (i = 0; i < objects.length; i++) {
//					next = objects[i];
//					result = imageEdit.getFbImage(next);
//					if (result) {
//						return result;
//					}
//				}
			}
			return null;
		};

		/**
		 * Ensures that the overlay image is correctly positioned.
		 * The overlay MUST be the correct aspect ratio.
		 * @private
		 * @function
		 */
		function positionOverlay() {
			var overlay = fbCanvas.overlayImage,
				width = fbCanvas.getWidth();
			if (overlay) {
				overlay.scaleToWidth(width).setCoords();
				fbCanvas.renderAll();
			}
		}

		/**
		 * Show or hide the overlay image.
		 * @param fabricCanvas The FabricJS canvas.
		 * @param show If truthy unhides (shows) the overlay.
		 */
		function showHideOverlay(fabricCanvas, show) {
			var overlay = fabricCanvas.overlayImage;
			if (overlay) {
				fabricCanvas.overlayImage.visible = !!show;
				fabricCanvas.renderAll();
			}
		}

		function getDialogFrameConfig(onclose) {
			return i18n.translate("imgedit_title").then(function(title) {
				return {
					onclose: onclose,
					id: "wc_img_editor",
					modal: true,
					resizable: true,
					title: title
				};
			});
		}

		function getEditorContext(config, callbacks) {
			if (config.inline) {
				var contentContainer = document.getElementById(config.id);
				if (contentContainer) {
					return Promise.resolve(callbacks.render(contentContainer));
				}
				return Promise.reject("Can not find element", config.id);
			}
			return getDialogFrameConfig(function() {
				imageCapture.stop();
				callbacks.lose();
			}).then(function(dialogConfig) {
				callbacks.rendered = function() {
					dialogFrame.reposition();
				};
				if (dialogFrame.isOpen()) {
					return callbacks.render(dialogFrame.getContent());
				}
				return dialogFrame.open(dialogConfig).then(function() {
					return callbacks.render(dialogFrame.getContent());
				});
			});
		}


		/**
		 * Builds the editor DOM and displays it to the user.
		 * @param {Object} config Map of configuration properties.
		 * @param {Object} callbacks An object with two callbacks: "win" and "lose".
		 * @param {File} file The file being edited.
		 * @returns {Promise} Resolved with the top level editor DOM element when it is ready.
		 * @function
		 * @private
		 */
		function getEditor(config, callbacks, file) {
			callbacks.render = renderEditor;

			function renderEditor(contentContainer) {
				var result = new Promise(function (win, lose) {
					var container = document.body.appendChild(document.createElement("div")),
						editorProps = {
							style: {
								width: config.width,
								height: config.height
							},
							feature: {
								face: false,
								rotate: config.rotate,
								zoom: config.zoom,
								move: config.move,
								redact: config.redact,
								reset: config.reset,
								undo: config.undo,
								cancel: config.cancel,
								save: config.save
							}
						},
						done = function(cntnr) {
							var eventConfig = attachEventHandlers(cntnr);
							zoomControls(eventConfig);
							moveControls(eventConfig);
							resetControl(eventConfig);
							cancelControl(eventConfig, cntnr, callbacks, file);
							saveControl(eventConfig, cntnr, callbacks, file);
							rotationControls(eventConfig);
							if (config.redactor) {
								config.redactor.controls(eventConfig, cntnr);
							}

							if (!file) {
								classList.add(cntnr, "wc_camenable");
								classList.add(cntnr, "wc_showcam");
								imageCapture.snapshotControl(eventConfig, cntnr);
							}


							if (contentContainer && cntnr) {
								contentContainer.innerHTML = "";
								contentContainer.appendChild(cntnr);
								if (callbacks.rendered) {
									callbacks.rendered(contentContainer);
								}
							}
							win(cntnr);
						};
					try {
						return getTranslations(editorProps).then(function() {
							container.className = "wc_img_editor";
							template.process({
								source: TEMPLATE_NAME,
								loadSource: true,
								target: container,
								context: editorProps,
								callback: function() {
									done(container);
								}
							});
							return container;
						}, lose);
					} catch (ex) {
						lose(ex);
					}
				});
				return result;  // a promise
			}  // end "renderEditor"
			return getEditorContext(config, callbacks);
		}

		function getTranslations(obj) {
			var messages = ["imgedit_action_camera", "imgedit_action_cancel", "imgedit_action_redact",
				"imgedit_action_redo", "imgedit_action_reset", "imgedit_action_save", "imgedit_action_snap", "imgedit_action_undo",
				"imgedit_capture", "imgedit_message_camera", "imgedit_message_cancel", "imgedit_message_move_center", "imgedit_message_move_down",
				"imgedit_message_move_left", "imgedit_message_move_right", "imgedit_message_move_up",
				"imgedit_message_nocapture", "imgedit_message_redact", "imgedit_message_redo", "imgedit_message_reset",
				"imgedit_message_rotate_left", "imgedit_message_rotate_left90", "imgedit_message_rotate_right",
				"imgedit_message_rotate_right90", "imgedit_message_save", "imgedit_message_snap",
				"imgedit_message_undo", "imgedit_message_zoom_in", "imgedit_message_zoom_out", "imgedit_move",
				"imgedit_move_center", "imgedit_move_down", "imgedit_move_left", "imgedit_move_right", "imgedit_move_up", "imgedit_redact",
				"imgedit_rotate", "imgedit_rotate_left", "imgedit_rotate_left90", "imgedit_rotate_right",
				"imgedit_rotate_right90", "imgedit_zoom", "imgedit_zoom_in", "imgedit_zoom_out"];
			return i18n.translate(messages).then(function(translations) {
				var result = obj || {};
				messages.forEach(function(message, idx) {
					result[message] = translations[idx];

				});
				return result;
			});
		}

		/**
		 * Wire up event listeners for the editor.
		 * @param {Element} container The top level editor DOM element.
		 * @returns {Object} An object used to map events to actions.
		 * @function
		 * @private`
		 */
		function attachEventHandlers(container) {
			var eventTimer,
				MAX_SPEED = 10,
				MIN_SPEED = 0.5,
				START_SPEED = 1.5,
				speed = START_SPEED,
				eventConfig = {
					press: {},
					click: {}
				};
			event.add(container, "mousedown", pressStart);
			event.add(container, "touchstart", pressStart);
			event.add(container, "mouseout", pressEnd);
			event.add(container, "click", clickEvent);
			event.add(document.body, "mouseup", pressEnd);
			event.add(document.body, "touchcancel", pressEnd);
			event.add(container, "touchend", pressEnd);

			function clickEvent($event) {
				var element = $event.target,
					config = getEventConfig(element, "click");
				if (!config) {
					element = BUTTON.findAncestor(element);
					config = getEventConfig(element, "click");
				}
				if (config) {
					pressEnd();
					eventTimer = timers.setTimeout(config.func.bind(this, config, $event), 0);
				}
			}

			function callbackWrapper(config) {
				config.func(config, speed);
				// Speed up while the button is being held down
				speed += (speed * 0.1);
				if (speed < MIN_SPEED) {
					speed = MIN_SPEED;
				} else if (speed > MAX_SPEED) {
					speed = MAX_SPEED;
				}
			}

			function pressStart($event) {
				var target = $event.target,
					element = BUTTON.findAncestor(target),
					config= getEventConfig(element, "press");
				if (config) {
					pressEnd();
					eventTimer = timers.setInterval(callbackWrapper, 100, config);
				}
			}

			function pressEnd() {
				speed = START_SPEED;
				if (eventTimer) {
					timers.clearInterval(eventTimer);
				}
			}

			function getEventConfig(element, type) {
				if (!element) {
					return null;
				}
				var name = element.name;
				if ((element.localName === "button" || element.type === "checkbox") && name && eventConfig[type]) {
					return eventConfig[type][name];
				}
				return null;
			}

			return eventConfig;
		}

		/*
		 * Get the angle to set when we want to rotate an image (which may already be rotated) to the next multiple
		 * of step.
		 *
		 * @param {Number} currentValue The current angle of rotation.
		 * @param {Number} step The angle of unit rotation, eg 90 or 45 (or Math.PI if you are really odd).
		 * @returns {Number} The number of degrees to which we want to set the item being rotated.
		 */
		function rotateToStepHelper(currentValue, step) {
			var interim;

			if (!step) {
				return currentValue; // no step why are you calling me?
			}

			if (!currentValue) { // start at 0
				return step;
			}

			if (currentValue % step === 0) { // current value is already a multiple of step so everything is easy.
				return currentValue + step;
			}

			interim = currentValue + step; // this is a simple rotate by step, now we need to work out where we should be.
			return Math.floor(interim / step) * step;
		}

		/*
		 * Helper for features that change numeric properties of the image on the canvas.
		 */
		function numericProp(config, speed) {
			var newValue,
				currentValue,
				fbImage = imageEdit.getFbImage(),  // this could be a group, does it matter?
				getter = config.getter || ("get" + config.prop),
				setter = config.setter || ("set" + config.prop),
				step = config.step || 1; // do not allow step to be 0
			if (fbImage) {
				currentValue = fbImage[getter]();
				if (config.exact) {
					newValue = rotateToStepHelper(currentValue, step);
				} else if (speed) {
					newValue = currentValue + (step * speed);
				} else {
					newValue = currentValue + step;
				}
				if (config.min) {
					newValue = Math.max(config.min, newValue);
				}
				fbImage[setter](newValue);
				imageEdit.renderCanvas(function() {
					if (undoRedo) {
						undoRedo.save();
					}
				});
				// fbCanvas.calcOffset();
			}
		}

		/*
		 * Wires up the "move" feature.
		 */
		function moveControls(eventConfig) {
			var press = eventConfig.press,
				click = eventConfig.click;
			press.up = {
				func: numericProp,
				prop: "Top",
				step: -1
			};

			press.down = {
				func: numericProp,
				prop: "Top",
				step: 1
			};

			press.left = {
				func: numericProp,
				prop: "Left",
				step: -1
			};

			press.right = {
				func: numericProp,
				prop: "Left",
				step: 1
			};

			click.center = {
				func: function() {
					var fbImage = imageEdit.getFbImage();
					if (fbImage) {
						fbImage.center();
					}
				}
			};
		}

		/*
		 * Wires up the "zoom" feature.
		 */
		function zoomControls(eventConfig) {
			var press = eventConfig.press;
			press.in = {
				func: numericProp,
				getter: "getScaleX",
				setter: "scale",
				step: 0.05
			};

			press.out = {
				func: numericProp,
				getter: "getScaleX",
				setter: "scale",
				step: -0.05,
				min: 0.1
			};
		}

		/*
		 * Wires up the "rotation" feature.
		 */
		function rotationControls(eventConfig) {
			var press = eventConfig.press;
			press.clock = {
				func: numericProp,
				prop: "Angle",
				step: 1
			};

			press.anticlock = {
				func: numericProp,
				prop: "Angle",
				step: -1
			};

			var click = eventConfig.click;
			click.clock90 = {
				func: numericProp,
				prop: "Angle",
				step: 90,
				exact: true
			};

			click.anticlock90 = {
				func: numericProp,
				prop: "Angle",
				step: -90,
				exact: true
			};
		}

		/*
		 * Wires up the "reset/undo/redo" feature.
		 */
		function resetControl(eventConfig) {
			var click = eventConfig.click;
			click.undo = {
				func: function() {
					if (undoRedo) {
						undoRedo.undo();
					}
				}
			};
			click.redo = {
				func: function() {
					if (undoRedo) {
						undoRedo.redo();
					}
				}
			};
			click.reset = {
				func: function() {
					if (undoRedo) {
						undoRedo.reset();
					}
				}
			};
		}

		/*
		 * Wires up the "cancel" feature.
		 */
		function cancelControl(eventConfig, editor, callbacks/* , file */) {
			var click = eventConfig.click,
				cancelFunc = function() {
					try {
						saveImage(editor, callbacks, true);
					} finally {
						dialogFrame.close();
					}
				};
			click.cancel = {
				func: cancelFunc
			};
		}

		/*
		 * Wires up the "save" feature.
		 */
		function saveControl(eventConfig, editor, callbacks, file) {
			var click = eventConfig.click,
				saveFunc = function() {
					saveImage(editor, callbacks, false, file);
				};
			click.save = {
				func: function () {
					if (imageEdit.getFbImage()) {
						if (callbacks.validate) {
							showHideOverlay(fbCanvas);  // This hide is for the validation, not the save.
							callbacks.validate(fbCanvas.getElement()).then(function(error) {
								if (error) {
									showHideOverlay(fbCanvas, true);  // Unhide the overlay post validation (save will have to hide it again).
									if (error.ignorable) {
										prompt.confirm(error, function(ignoreValidationError) {
											if (ignoreValidationError) {
												saveFunc();
											} else {
												callbacks.lose();
											}
										});
									} else {
										callbacks.lose(error);
									}
								} else {
									saveFunc();
								}

							}, function() {
								callbacks.lose();
							});
						} else {
							saveFunc();
						}
					} else {
						// we should only be here if the user has not taken a snapshot from the video stream
						i18n.translate("imgedit_noimage").then(function(message) {
							prompt.alert(message);
						});
					}
				}
			};
		}

		/**
		 * The exit point of the editor, either save or cancel the edit.
		 * @param {Element} editor The top level container element of the editor component.
		 * @param {Object} callbacks "win" and "lose".
		 * @param {boolean} cancel Cease all editing, the user wishes to cancel.
		 * @param {File} [file] The binary file being edited.
		 */
		function saveImage(editor, callbacks, cancel, file) {
			var result, done = function() {
					fbCanvas = null;  // = canvasElement
					imageCapture.stop();
					editor.parentNode.removeChild(editor);
				};
			try {
				if (cancel) {
					done();
					callbacks.lose();
				} else {
					if (fbCanvas.getActiveObject()) {
						fbCanvas.deactivateAll();  // selection box should not be part of the image
						fbCanvas.renderAll();  // got to render for the selection box to disappear
					}
					showHideOverlay(fbCanvas);
					if (file && !hasChanged()) {
						console.log("No changes made, using original file");
						result = file;  // if the user has made no changes simply pass thru the original file.
					} else {
						result = saveCanvasAsFile(file);
					}
					done();
					callbacks.win(result);
				}
			} finally {
//				dialogFrame.close();
				dialogFrame.resetContent();
			}
		}

		/**
		 * Before saving the image we may wish to discard any scaling the user has performed.
		 * This function removes scaling on the image and preserves relative ratios with other objects on the canvas.
		 * @param {fabric.Image} fbImage The image to un-scale.
		 */
		function unscale(fbImage) {
			var originaSize = fbImage.getOriginalSize(),
				objects = fbCanvas.getObjects().map(function(o) {
					return o.set("active", true);
				}),
				group = new fabric.Group(objects, {
					originX: "left",
					originY: "top"
				});
			fbCanvas.setActiveGroup(group.setCoords()).renderAll();
			group.scaleToWidth(originaSize.width);
			group.scaleToHeight(originaSize.height);
			return group;
		}

		function saveCanvasAsFile(file) {
			var result, toDataUrlParams, config, object,
				fbImage = imageEdit.getFbImage();
			if (fbImage) {
				config = imageEdit.getConfig();
				if (config.crop) {
					object = fbImage;
					toDataUrlParams = {
						left: 0,
						top: 0,
						width: Math.min(fbCanvas.getWidth(), object.getWidth()),
						height: Math.min(fbCanvas.getHeight(), object.getHeight())
					};
				} else {
					object = unscale(fbImage);
					toDataUrlParams = {
						left: object.getLeft(),
						top: object.getTop(),
						width: object.getWidth(),
						height: object.getHeight()
					};
				}
				// Add params such as format, quality, multiplier etc
				toDataUrlParams = mixin(toDataUrlParams, config);

				// canvasElement = fbCanvas.getElement();
				// result = canvasElement.toDataURL();
				result = fbCanvas.toDataURL(toDataUrlParams);
				result = dataURItoBlob(result);
				result = blobToFile(result, file);
			}
			return result;
		}

		/**
		 * Determine if the user has actually made any changes to the image in the editor.
		 * @returns {boolean} true if the user has made changes.
		 */
		function hasChanged() {
			return undoRedo && undoRedo.hasChanges();
		}

		/**
		 * Converts an img element to a File blob.
		 * @param {Element} element An img element.
		 * @returns {File} The image as a binary File.
		 */
		function imgToFile(element) {
			var scale = 1,
				canvas = document.createElement("canvas"),
				context, file, dataUrl, blob, config = {
					name: element.id
				};
			if (element && element.src) {
				canvas.width = element.naturalWidth * scale;
				canvas.height = element.naturalHeight * scale;
				context = canvas.getContext("2d");
				context.drawImage(element, 0, 0);
				dataUrl = canvas.toDataURL("image/png");
				blob = dataURItoBlob(dataUrl);
				file = blobToFile(blob, config);
			}
			return file;
		}

		/**
		 * Converts a generic binary blob to a File blob.
		 * @param {Blob} blob
		 * @param {Object} [config] Attempt to set some of the file properties such as "type", "name"
		 * @returns {File} The File blob.
		 */
		function blobToFile(blob, config) {
			var name,
				filePropertyBag = {
					type: blob.type,
					lastModified: new Date()
				};
			if (config) {
				if (!filePropertyBag.type) {
					filePropertyBag.type = config.type;
				}
				name = config.name;
			}
			name = name || uid();


//			if (typeof File === "function") {
//				return new File([blob], name, filePropertyBag);
//			}
			if (!blob.type) {
				// noinspection JSAnnotator
				blob.type = filePropertyBag.type;
			}
			blob.lastModifiedDate = filePropertyBag.lastModified;
			blob.lastModified = filePropertyBag.lastModified.getTime();
			blob.name = name;
			checkFileExtension(blob);
			return blob;
		}

		/**
		 * Converts a data url to a binary blob.
		 * @param {string} dataURI
		 * @returns {Blob} The binary blob.
		 */
		function dataURItoBlob(dataURI) {
			// convert base64/URLEncoded data component to raw binary data held in a string
			var byteString, mimeString, ia, i;
			if (dataURI.split(",")[0].indexOf("base64") >= 0) {
				byteString = atob(dataURI.split(",")[1]);
			} else {
				byteString = unescape(dataURI.split(",")[1]);
			}

			// separate out the mime component
			mimeString = dataURI.split(",")[0].split(":")[1].split(";")[0];

			// write the bytes of the string to a typed array
			ia = new window.Uint8Array(byteString.length);
			for (i = 0; i < byteString.length; i++) {
				ia[i] = byteString.charCodeAt(i);
			}

			return new Blob([ia], { type: mimeString });
		}

		/**
		 * Ensures that the file name ends with an extension that matches its mime type.
		 * @param file The file to check.
		 */
		function checkFileExtension(file) {
			var expectedExtension,
				info = getMimeType({
					files: [file]
				});
			if (info && info.length) {
				info = info[0];
				if (info.mime && imageEdit.mimeToExt[info.mime]) {
					expectedExtension = imageEdit.mimeToExt[info.mime];
					if (expectedExtension !== info.ext) {
						file.name += "." + expectedExtension;
					}
				}
			}
		}
	}
	return imageEdit;
});
