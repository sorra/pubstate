import Common from '../common.js'

export class ProfileEditor {
  constructor() {
    const $roots = $('#user-name-control, #user-intro-control')
    $roots.each(function () {
      var $root = $(this)

      $root.find('.edit-btn').click(function () {
        // Fill display value to input element
        let value = $root.find('.display-bar .display-value').text()
        $root.find('.edit-bar .input-el').val(value)
        // Toggle elements
        $root.find('.display-bar').hide()
        $root.find('.edit-bar').show()
      })

      $root.find('.edit-bar .confirm-btn').click(function (ev) {
        ev.preventDefault()
        const $btn = $(this)
        const $input = $root.find('.edit-bar .input-el')
        const name = $input.attr('name')
        const value = $input.val()
        let payload = {}
        payload[name] = value

        $.post('/profile', payload)
          .always(() => {
            $btn.prop('disabled', false)
          })
          .done(resp => {
            // Backfill input value to display element
            let value = $root.find('.edit-bar .input-el').val()
            $root.find('.display-bar .display-value').text(value)

            $root.find('.display-bar').show()
            $root.find('.edit-bar').hide()
          })
          .fail(resp => {
            Common.popAlert(resp, 'error')
          })

        $btn.prop('disabled', true)
      })

      $root.find('.edit-bar .cancel-btn').click(function (ev) {
        ev.preventDefault()
        const $btn = $(this)

        $root.find('.display-bar').show()
        $root.find('.edit-bar').hide()
      })
    })
  }
}

window.ProfileEditor = ProfileEditor