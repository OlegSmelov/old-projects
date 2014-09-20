RSpec::Matchers.define :run do |method_name|
  match do |object|
    result = object.method(method_name.to_sym).call
    if @direction == :unsuccessfully
      result == false
    else
      result == true
    end
  end

  failure_message_for_should do |object|
    "expected that #{object.class} would " + @direction.to_s + " run " + method_name.to_s
  end

  failure_message_for_should_not do |object|
    "expected that #{object.class} would not " + @direction.to_s + " run " + method_name.to_s
  end

  description do
    @direction.to_s + " " + method_name.to_s
  end

  def method_missing(sym, *args, &block)
    @direction = sym
    self
  end
end