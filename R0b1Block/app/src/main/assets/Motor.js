Blockly.JavaScript['Motor'] = function(block) {
  var dropdown_port = block.getFieldValue('Port');
  var value_power = Blockly.JavaScript.valueToCode(block, 'Power', Blockly.JavaScript.ORDER_ATOMIC);
  // TODO: Assemble JavaScript into code variable.
  var code = 'Motor.set(dropdown_port, value_power);\n';
  return code;
};