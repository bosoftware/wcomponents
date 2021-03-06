define(["wc/has",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/tag",
	"wc/dom/Widget",
	"wc/Observer",
	"wc/ui/validation/feedback"],
	function(has, initialise, shed, tag, Widget, Observer, feedback) {
		"use strict";

		/**
		 * This module know when to mark things and valid/invalid but not "how".
		 * Good rule of thumb, if you import "i18n" or "classList" or build DOM snippets in here you should rethink it.
		 * @constructor
		 * @alias module:wc/ui/validation/validationManager~ValidationManager
		 * @private
		 */
		function ValidationManager() {
			var
				/**
				 * At its heart validationManager is just an observer surrogate and this is the instance of
				 * {@link module:wc/Observer} used to subscribe and publish.
				 *
				 * @var
				 * @type {module:wc/Observer}
				 * @private
				 */
				observer,
				/**
				 * The description of a FORM. Instantiated on first use.
				 * @constant
				 * @type {module:wc/dom/Widget}
				 * @private
				 */
				FORM,
				/**
				 * The description of a component in an invalid state.
				 * @constant
				 * @type {module:wc/dom/Widget}
				 * @private
				 */
				INVALID_COMPONENT = new Widget("", "", { "aria-invalid": "true" }),
				REVALIDATE_OBSERVER_GROUP = "reval";

			this.setOK = feedback.setOk;  // reimplement legacy API
			this.flagError = feedback.flagError;  // reimplement legacy API


			/**
			 * Listen for DISABLE, HIDE or OPTIONAL actions and clear any error message for the component.
			 * @function
			 * @private
			 * @param {Element} element The element being acted upon.
			 */
			function shedSubscriber(element) {
				if (element && INVALID_COMPONENT.isOneOfMe(element)) {
					feedback.clearError(element);
				}
			}

			/**
			 * An element is exempt from participating in client side validation if:
			 * <ol><li>the element in INPUT type hidden;</li>
			 * <li>the element is disabled; or</li>
			 * <li>the element is not 'visible' (do shed test first - it is quicker).</li>
			 * </ol>
			 *
			 * @function module:wc/ui/validation/validationManager.isExempt
			 * @param {Element} element The component to test.
			 * @returns {Boolean} true if the component is exempt from client side validation.
			 */
			this.isExempt = function(element) {
				var result = false;
				if ((element.tagName === tag.INPUT && element.type === "hidden") || shed.isDisabled(element) || shed.isHidden(element)) {
					result = true;
				}
				return result;
			};


			/**
			 * Is an element currently in an invalid state? This is used to indicate that revalidation may be needed
			 * (commonly for a change event listener). NOTE: this does not test the validity of the element, merely
			 * returns whether anything has put the element into an invalid state previously.
			 *
			 * @function module:wc/ui/validation/validationManager.isInvalid
			 * @param {Element} element The component to test for validity.
			 * @returns {Boolean} true if the element is invalid.
			 */
			this.isInvalid = function(element) {
				return INVALID_COMPONENT.isOneOfMe(element);
			};



			/**
			 * Most validating components have a pretty similar mechanism to revalidate whern their input changes so
			 * this helper exists to take care of it.
			 *
			 * @function module:wc/ui/validation/validationManager.revalidationHelper
			 * @param {Element} element The component being re-validated.
			 * @param {Function} _validateFunc The component's validation function.
			 */
			this.revalidationHelper = function(element, _validateFunc) {
				var initiallyInvalid = this.isInvalid(element),
					isNowInvalid = initiallyInvalid;

				if (initiallyInvalid) {
					if ((_validateFunc(element))) {
						feedback.setOK(element);
						isNowInvalid = false;
					} else {
						isNowInvalid = true;
					}
				} else if (feedback.isMarkedOK(element, this)) {
					isNowInvalid = !_validateFunc(element);
				}

				if (observer && isNowInvalid !== initiallyInvalid) { // if the current component's validity has changed
					observer.setFilter(REVALIDATE_OBSERVER_GROUP);
					observer.notify(element);
				}
			};


			/**
			 * Tests the validity of form bound elements within a specified container.
			 *
			 * @function module:wc/ui/validation/validationManager.isValid
			 * @param {Element} [container] A DOM node (preferably containing form controls). If the container is not
			 *                   specified finds the form containing the activeElement (this is for use with controls
			 *                   with submitOnchange).
			 * @returns {Boolean} true if the container is in a valid state (all components in the container which
			 *                   support validation are valid).
			 */
			this.isValid = function (container) {
				var result = true;

				/**
				 * Observer callback function to keep track of validity from all subscribers. A container is only valid
				 * if all of its subscribers return true.
				 * @function
				 * @private
				 * @param {Boolean} decision true if valid.
				 * @returns {bitmap}
				 */
				function _callback(decision) {
					result &= decision;  // we are only valid if all observers are valid
				}

				if (!container) {
					FORM = FORM || new Widget("form");
					container = FORM.findAncestor(document.activeElement);
				}
				if (container && observer) {
					observer.setCallback(_callback);
					observer.notify(container);
				}

				result = !!result;  // convert the potentially bitwise result to a Boolean

				if (!result && repainter) {  // IE8 has repaint issues when validation errors are inserted into columns
					repainter.checkRepaint(container);
				}
				return result;
			};

			/**
			 * Late intialisation callback to subscribe to shed to listen for state changes which impact any existing
			 * validation error messages.
			 * @function module:wc/ui/validation/validationManager.postInit
			 */
			this.postInit = function() {
				shed.subscribe(shed.actions.DISABLE, shedSubscriber);
				shed.subscribe(shed.actions.HIDE, shedSubscriber);
				shed.subscribe(shed.actions.OPTIONAL, shedSubscriber);
			};

			/**
			 * Allows a component to subscribe to client side validation.
			 * @function module:wc/ui/validation/validationManager.subscribe
			 * @see {@link module:wc/Observer#subscribe}
			 *
			 * @param {Function} subscriber The function that will be notified by validationManager. This function MUST
			 *                   be present at "publish" time, but need not be present at "subscribe" time.
			 * @param {boolean} [revalidate] if truthy subscribe to revalidation rather than validation.
			 * @returns {?Function} A reference to the subscriber.
			 */
			this.subscribe = function(subscriber, revalidate) {
				observer = observer || new Observer();
				var group = revalidate ? { group: REVALIDATE_OBSERVER_GROUP } : null;
				return observer.subscribe(subscriber, group);
			};
		}

		var instance, repainter;

		/* ie8's interesting inline-block bug means we need to force a repaint after all validating activites.*/
		if (has("ie") === 8) {
			require(["wc/fix/inlineBlock_ie8"], function(inlineBlock) {
				repainter = inlineBlock;
			});
		}

		/**
		 * Generic client side validation manager. This is the publisher for client side validation. Any component which
		 * requires validation subscribes to this using validationManager.subscribe.
		 *
		 * @module wc/ui/validation/validationManager
		 * @requires module:wc/dom/classList
		 * @requires module:wc/dom/getBox
		 * @requires module:wc/has"
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/tag
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/Observer
		 * @requires external:lib/sprintf
		 * @requires module:wc/i18n/i18n
		 */
		instance = new ValidationManager();
		initialise.register(instance);
		return instance;
	});
