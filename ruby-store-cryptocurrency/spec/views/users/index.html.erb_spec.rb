describe "users/index" do
  before :each do
    @users = [create(:user), create(:user)]
    assign(:users, @users)
  end

  it "shows account names and full names of users in a table" do
    render
    @users.each do |user|
      expect(rendered).to have_selector("table tbody tr td", text: user.name)
      expect(rendered).to have_selector("table tbody tr td", text: user.full_name)
    end
  end

  it "doesn't show emails" do
    render
    @users.each do |user|
      expect(rendered).to_not include user.email
    end
  end
end