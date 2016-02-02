/**
 * Impl hand rolled.
 */
goog.module('vmbootstrap.primitives.$byte$impl');


let Class = goog.forwardDeclare('gen.java.lang.Class$impl');


/**
 * Placeholder class definition for the primitive class byte.
 *
 * Non-instantiable.
 */
class $byte {
  /**
   * Defines instance fields.
   *
   * @private
   */
  constructor() {}

  /**
   * Returns whether the provided instance is an instance of this class.
   *
   * @param {*} instance
   * @return {boolean}
   * @public
   */
  static $isInstance(instance) { return typeof instance === 'number'; }

  /**
   * Returns whether the provided class is or extends this class.
   *
   * @param {Function} classConstructor
   * @return {boolean}
   * @private
   */
  static $isAssignableFrom(classConstructor) {
    return classConstructor === $byte;
  }

  /**
   * @return {Class}
   * @public
   */
  static $getClass() {
    $byte.$clinit();
    if (!$byte.$classByte_) {
      $byte.$classByte_ = Class.$createForPrimitive('byte');
    }
    return $byte.$classByte_;
  }

  /**
   * Runs inline static field initializers.
   * @protected
   */
  static $clinit() {
    Class = goog.module.get('gen.java.lang.Class$impl');
  }
};


/**
 * The class literal field.
 * @private {Class}
 */
$byte.$classByte_ = null;


/**
 * @public {number}
 */
$byte.$initialArrayValue = 0;


/**
 * Exported class.
 */
exports = $byte;

