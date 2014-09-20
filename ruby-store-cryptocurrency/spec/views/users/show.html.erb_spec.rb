describe "users/show" do
  let(:user) { create(:user) }
  
  before :each do
    assign(:user, user)
    allow(view).to receive(:current_user).and_return(user)
  end

  it "displays user information correctly" do
    render
    expect(rendered).to have_selector("td", text: user.name)
    expect(rendered).to have_selector("td", text: user.full_name)
    expect(rendered).to have_selector("td", text: user.email)
  end

  it "disallows to view other user's information" do
    assign(:user, create(:user))
    render
    expect(rendered).to have_selector("h2", text: "Information is private")
  end

  it "shows the number of bought products" do
    7.times { user.bought_products << create(:product) }
    render
    expect(rendered).to have_selector("td", text: "7")
  end
end