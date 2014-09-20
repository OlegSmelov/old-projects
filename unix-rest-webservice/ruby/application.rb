require 'net/http'

class Controller

  attr_accessor :host, :port, :token

  def initialize(host, port, token)
    self.host = host
    self.port = port
    self.token = token
  end

  def get(path, params = {})
    uri = generate_uri(path, params)    
    response = Net::HTTP.new(uri.host, uri.port).start do |http|
      request = Net::HTTP::Get.new(uri.to_s, initheader = {'Content-Type' =>'text/plain'})
      http.request(request) 
    end 
    response
  end

  def post(path, body, params = {})
    uri = generate_uri(path, params)
    response = Net::HTTP.new(uri.host, uri.port).start do |http| 
      request = Net::HTTP::Post.new(uri.to_s, initheader = {'Content-Type' =>'text/plain'})
      request.body = body
      http.request(request) 
    end
    response
  end

  def delete(path, params = {})
    uri = generate_uri(path, params)
    response = Net::HTTP.new(uri.host, uri.port).start do |http| 
      request = Net::HTTP::Delete.new(uri.to_s, initheader = {'Content-Type' =>'text/plain'})
      http.request(request) 
    end
    response
  end

  protected

  def generate_uri(path, params)
    raise "No host present" if host.nil?
    r = "http://#{host}"
    r << ":#{port}" if port
    r << path if path
    r << "?token=#{token}"
    params.each do |key, value|
      r << "&#{key}=#{value}"
    end
    r = URI.parse(r)
  end

end

access_token = 'TT1CpCLYcTZF3if3Y5fA'

c = Controller.new('localhost', '3333', access_token)

puts "GET /tetris"
response = c.get('/tetris')
puts response.body

post_body = "Andrius|Janauskas|1337"
puts "POST /tetris #{post_body}"
response = c.post('/tetris', post_body)
puts response.body

if response.body.include?("Row id:") # success
  id = response.body.split(":")[1].to_i

  puts "DELETE /tetris/#{id}"
  response = c.delete("/tetris/#{id}")
  puts response.body
end
